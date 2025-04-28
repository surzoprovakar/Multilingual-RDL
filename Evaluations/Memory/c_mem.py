import matplotlib.pyplot as plt
import matplotlib.ticker as mticker

# Data
operations = [2000, 4000, 6000, 8000, 10000]
babel_crdt = [43.33333333,
44.1171875,
45.8141276,
46.71875,
47.50130208]
go_crdt = [63.22070313,
65.42773438,
68.6875,
71.55078125,
76.65820313]
legion = [116.1464844,
116.6445313,
118.0078125,
118.4160156,
120.3847656]
t_crdt = [108.5244141,
109.4355469,
113.2050781,
114.7685547,
117.46875]

# Plotting the data
# plt.figure(figsize=(8, 5))
plt.plot(operations, go_crdt, color='maroon', linestyle=':', linewidth=3, marker='*',markersize=15, label='Strategy I (Go-CRDT)')
plt.plot(operations, legion, color='chocolate', linestyle='--', linewidth=3, marker='s',markersize=10, label='Strategy I (Legion)')
plt.plot(operations, t_crdt, color='blue', linestyle='-.', linewidth=3, marker='D',markersize=10, label='Strategy I (T-CRDT)')
plt.plot(operations, babel_crdt, color='teal', linestyle='-', linewidth=3, marker='o',markersize=10, label='Strategy II (CDF-RDL)')

# Setting font size for X and Y axis
plt.xlabel('# of operations', fontsize=20)

# Format x-axis labels as "2k", "4k", etc.
def format_func(value, tick_number):
    return f'{int(value/1000)}k'

plt.gca().xaxis.set_major_formatter(mticker.FuncFormatter(format_func))
plt.ylabel('Memory Usage (MB)', fontsize=20)

plt.xticks(fontsize=16)
plt.yticks(fontsize=16)

# Adding legend with font size 16
plt.legend(fontsize=14, loc='upper center', bbox_to_anchor=(0.5, 1.20), ncol=2,
           fancybox=True, edgecolor='black')

# Displaying the grid
plt.grid(True)
plt.tight_layout()

plt.gcf().set_size_inches(8, 6)
# Saving the figure as a PDF with specified size
plt.savefig('counter_memory.pdf', format='pdf', dpi=300, bbox_inches='tight')

# Showing the plot
plt.show()
