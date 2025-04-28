import matplotlib.pyplot as plt
import matplotlib.ticker as mticker

# Data
operations = [2000, 4000, 6000, 8000, 10000]
babel_crdt = [42.99869792, 43.09928385, 43.36263021, 43.71744792, 44.36588542]
go_crdt = [64.59179688, 65.85351563, 71.28125, 73.12304688, 75.27734375]
legion = [116.1855469, 116.7578125, 117.6972656, 119.1933594, 121.3515625]
t_crdt = [107.8984375, 108.6650391, 109.859375, 112.5498047, 114.4472656]

# Plotting the data
# plt.figure(figsize=(8, 5))
plt.plot(operations, go_crdt, color='maroon', linestyle=':', linewidth=3, marker='*',markersize=15, label='Strategy I (Go-CRDT)')
plt.plot(operations, legion, color='chocolate', linestyle='--', linewidth=3, marker='s',markersize=10, label='Strategy I (Legion)')
plt.plot(operations, t_crdt, color='blue', linestyle='-.', linewidth=3, marker='D',markersize=10, label='Strategy I (T-CRDT)')
plt.plot(operations, babel_crdt, color='teal', linestyle='-', linewidth=3, marker='o',markersize=10, label='Strategy II (CDF-RDL)')

# Setting font size for X and Y axis
plt.xlabel('# of operations', fontsize=20)
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
plt.savefig('set_memory.pdf', format='pdf', dpi=300, bbox_inches='tight')

# Showing the plot
plt.show()
