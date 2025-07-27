#!/usr/bin/env python3
"""
Post-processing script for StateVectorParallelBenchmark results.

This script analyzes JMH benchmark results and calculates speedup ratios
between parallel and serial execution modes. It generates both console
output and markdown reports with speedup tables.

Usage:
    python speedup.py <jmh_results.json> [--output report.md] [--min-speedup 2.8]

Requirements:
    - JMH results in JSON format
    - Python 3.6+ with json module (standard library)
"""

import json
import sys
import argparse
from typing import Dict, List, Tuple, Optional
from dataclasses import dataclass
from statistics import geometric_mean


@dataclass
class BenchmarkResult:
    """Represents a single benchmark result."""
    qubits: int
    gates: int
    parallel: bool
    score: float  # execution time in milliseconds
    error: float  # standard error


def parse_jmh_results(json_file: str) -> List[BenchmarkResult]:
    """
    Parse JMH JSON results into BenchmarkResult objects.
    
    Args:
        json_file: Path to JMH results JSON file
        
    Returns:
        List of BenchmarkResult objects
        
    Raises:
        FileNotFoundError: If JSON file doesn't exist
        json.JSONDecodeError: If JSON is malformed
        KeyError: If required fields are missing
    """
    with open(json_file, 'r') as f:
        data = json.load(f)
    
    results = []
    for benchmark in data:
        # Parse benchmark name to extract parameters
        # Expected format: "com.omaarr90.benchmarks.StateVectorParallelBenchmark.runCircuit"
        benchmark_name = benchmark['benchmark']
        
        # Extract parameters from benchmark params
        params = benchmark['params']
        qubits = int(params['qubits'])
        gates = int(params['gates'])
        parallel = params['parallel'].lower() == 'true'
        
        # Extract performance metrics
        primary_metric = benchmark['primaryMetric']
        score = primary_metric['score']  # Mean execution time
        error = primary_metric['scoreError']  # Standard error
        
        results.append(BenchmarkResult(qubits, gates, parallel, score, error))
    
    return results


def calculate_speedups(results: List[BenchmarkResult]) -> Dict[Tuple[int, int], Dict[str, float]]:
    """
    Calculate speedup ratios for each (qubits, gates) combination.
    
    Args:
        results: List of benchmark results
        
    Returns:
        Dictionary mapping (qubits, gates) to speedup metrics:
        {
            (qubits, gates): {
                'serial_time': float,
                'parallel_time': float,
                'speedup': float,
                'serial_error': float,
                'parallel_error': float
            }
        }
    """
    # Group results by (qubits, gates) combination
    grouped = {}
    for result in results:
        key = (result.qubits, result.gates)
        if key not in grouped:
            grouped[key] = {'serial': None, 'parallel': None}
        
        if result.parallel:
            grouped[key]['parallel'] = result
        else:
            grouped[key]['serial'] = result
    
    # Calculate speedups
    speedups = {}
    for key, group in grouped.items():
        serial = group['serial']
        parallel = group['parallel']
        
        if serial is None or parallel is None:
            print(f"Warning: Missing data for {key} - skipping speedup calculation")
            continue
        
        speedup = serial.score / parallel.score if parallel.score > 0 else 0.0
        
        speedups[key] = {
            'serial_time': serial.score,
            'parallel_time': parallel.score,
            'speedup': speedup,
            'serial_error': serial.error,
            'parallel_error': parallel.error
        }
    
    return speedups


def print_speedup_table(speedups: Dict[Tuple[int, int], Dict[str, float]]) -> None:
    """Print speedup results in a formatted table."""
    print("\n" + "="*80)
    print("STATEVECTOR PARALLEL EXECUTION SPEEDUP RESULTS")
    print("="*80)
    print(f"{'Qubits':<8} {'Gates':<8} {'Serial (ms)':<12} {'Parallel (ms)':<14} {'Speedup':<10}")
    print("-" * 80)
    
    # Sort by qubits, then gates
    sorted_keys = sorted(speedups.keys())
    
    speedup_values = []
    for key in sorted_keys:
        qubits, gates = key
        data = speedups[key]
        
        serial_time = data['serial_time']
        parallel_time = data['parallel_time']
        speedup = data['speedup']
        speedup_values.append(speedup)
        
        print(f"{qubits:<8} {gates:<8} {serial_time:<12.2f} {parallel_time:<14.2f} {speedup:<10.2f}")
    
    # Calculate geometric mean speedup for qubits >= 14
    large_circuit_speedups = [
        speedups[key]['speedup'] 
        for key in sorted_keys 
        if key[0] >= 14
    ]
    
    if large_circuit_speedups:
        geo_mean = geometric_mean(large_circuit_speedups)
        print("-" * 80)
        print(f"Geometric mean speedup (qubits ≥ 14): {geo_mean:.2f}×")
        
        if geo_mean >= 3.0:
            print("✅ PASS: Speedup target (≥3.0×) achieved!")
        elif geo_mean >= 2.8:
            print("⚠️  MARGINAL: Speedup within CI variance margin (≥2.8×)")
        else:
            print("❌ FAIL: Speedup below target (<2.8×)")
    
    print("="*80)


def generate_markdown_report(speedups: Dict[Tuple[int, int], Dict[str, float]], 
                           output_file: str) -> None:
    """Generate markdown report with speedup results."""
    with open(output_file, 'w') as f:
        f.write("# StateVector Parallel Execution Speedup Report\n\n")
        f.write("This report shows the performance comparison between parallel and serial execution ")
        f.write("of quantum circuits using the StateVector simulation engine.\n\n")
        
        f.write("## Benchmark Configuration\n\n")
        f.write("- **Benchmark Mode**: SingleShotTime\n")
        f.write("- **Warmup Iterations**: 5\n")
        f.write("- **Measurement Iterations**: 10\n")
        f.write("- **JVM Args**: `--enable-preview -Djdk.incubator.concurrent.enablePreview -Xms4g -Xmx4g`\n\n")
        
        f.write("## Results\n\n")
        f.write("| Qubits | Gates | Serial (ms) | Parallel (ms) | Speedup |\n")
        f.write("|--------|-------|-------------|---------------|----------|\n")
        
        # Sort by qubits, then gates
        sorted_keys = sorted(speedups.keys())
        
        speedup_values = []
        for key in sorted_keys:
            qubits, gates = key
            data = speedups[key]
            
            serial_time = data['serial_time']
            parallel_time = data['parallel_time']
            speedup = data['speedup']
            speedup_values.append(speedup)
            
            f.write(f"| {qubits} | {gates} | {serial_time:.2f} | {parallel_time:.2f} | {speedup:.2f}× |\n")
        
        # Calculate geometric mean speedup for qubits >= 14
        large_circuit_speedups = [
            speedups[key]['speedup'] 
            for key in sorted_keys 
            if key[0] >= 14
        ]
        
        if large_circuit_speedups:
            geo_mean = geometric_mean(large_circuit_speedups)
            f.write(f"\n**Geometric Mean Speedup (qubits ≥ 14): {geo_mean:.2f}×**\n\n")
            
            f.write("## Analysis\n\n")
            if geo_mean >= 3.0:
                f.write("✅ **PASS**: Speedup target (≥3.0×) achieved!\n\n")
                f.write("The parallel execution implementation successfully delivers the required ")
                f.write("performance improvement over serial execution.\n")
            elif geo_mean >= 2.8:
                f.write("⚠️ **MARGINAL**: Speedup within CI variance margin (≥2.8×)\n\n")
                f.write("The speedup is close to the target but may be affected by CI environment variance.\n")
            else:
                f.write("❌ **FAIL**: Speedup below target (<2.8×)\n\n")
                f.write("The parallel execution does not meet the minimum speedup requirements.\n")
        
        f.write("\n## Notes\n\n")
        f.write("- Circuits with ≤12 qubits automatically use serial execution (expected speedup ≈ 1.0×)\n")
        f.write("- Speedup measurements may vary based on CPU architecture and system load\n")
        f.write("- Results are based on geometric mean to account for different circuit sizes\n")


def main():
    parser = argparse.ArgumentParser(description='Analyze StateVector parallel benchmark results')
    parser.add_argument('json_file', help='JMH results JSON file')
    parser.add_argument('--output', '-o', help='Output markdown report file')
    parser.add_argument('--min-speedup', type=float, default=2.8, 
                       help='Minimum required speedup for CI (default: 2.8)')
    
    args = parser.parse_args()
    
    try:
        # Parse benchmark results
        results = parse_jmh_results(args.json_file)
        print(f"Parsed {len(results)} benchmark results from {args.json_file}")
        
        # Calculate speedups
        speedups = calculate_speedups(results)
        
        if not speedups:
            print("Error: No valid speedup data found")
            sys.exit(1)
        
        # Print results to console
        print_speedup_table(speedups)
        
        # Generate markdown report if requested
        if args.output:
            generate_markdown_report(speedups, args.output)
            print(f"\nMarkdown report generated: {args.output}")
        
        # Check if speedup meets minimum requirement for CI
        large_circuit_speedups = [
            speedups[key]['speedup'] 
            for key in speedups.keys() 
            if key[0] >= 14
        ]
        
        if large_circuit_speedups:
            geo_mean = geometric_mean(large_circuit_speedups)
            if geo_mean < args.min_speedup:
                print(f"\nCI FAILURE: Speedup {geo_mean:.2f}× below minimum {args.min_speedup}×")
                sys.exit(1)
            else:
                print(f"\nCI SUCCESS: Speedup {geo_mean:.2f}× meets minimum {args.min_speedup}×")
        
    except Exception as e:
        print(f"Error: {e}")
        sys.exit(1)


if __name__ == '__main__':
    main()