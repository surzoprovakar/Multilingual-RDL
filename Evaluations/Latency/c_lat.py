import matplotlib.pyplot as plt
import matplotlib.ticker as mticker

# Data
operations = [2000, 4000, 6000, 8000, 10000]
babel_crdt = [363.33, 470, 630, 730, 923.33]
go_crdt = [760,
1315,
1605,
1955,
2455]
legion = [1445,
1660,
2020,
2595,
3245]
t_crdt = [1125,
1465,
1785,
2360,
3100]

# # Bar width
# bar_width = 0.2

# # Positions of the bars on the x-axis
# r1 = np.arange(len(operations))
# r2 = [x + bar_width for x in r1]
# r3 = [x + bar_width for x in r2]
# r4 = [x + bar_width for x in r3]

# # Plotting the bars
# plt.figure(figsize=(8, 6))
# plt.bar(r1, babel_crdt, color='gray', width=bar_width, edgecolor='black', label=r'$\mathcal{B}$abelCRDT')
# plt.bar(r2, go_crdt, color='w', edgecolor='teal', hatch='//', width=bar_width, label='Go-CRDT')
# plt.bar(r3, legion, color='w', edgecolor='chocolate', hatch='--', width=bar_width, label='Legion')
# plt.bar(r4, t_crdt, color='w', edgecolor='blue', hatch='xx', width=bar_width, label='T-CRDT')

# # Adding labels and title
# plt.xlabel('# of operations', fontsize=20)
# plt.ylabel('Average Latency (ms)', fontsize=20)
# # plt.title('Performance Comparison of CRDT Implementations', fontsize=20)
# plt.xticks([r + 1.5 * bar_width for r in range(len(operations))], operations, fontsize=16)
# plt.yticks(fontsize=16)

# # Adding legend with font size 17
# plt.legend(fontsize=20, loc='upper center', bbox_to_anchor=(0.5, 1.28), ncol=2,
#            fancybox=True, edgecolor='black')
# # plt.legend(fontsize=17)

# # Adjust layout to make space for the legend
# # plt.tight_layout(rect=[0, 0, 1, 0.88])

# # Displaying the grid
# plt.grid(True, linestyle='--', alpha=0.6)

# # Saving the figure as a PDF with specified size
# plt.tight_layout()
# plt.savefig('counter_latency.pdf', format='pdf', dpi=300)

# # Showing the plot
# plt.show()

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
plt.ylabel('Average Latency (Sec)', fontsize=20)

plt.xticks(fontsize=16)
plt.yticks(fontsize=16)

# Adding legend with font size 16
plt.legend(fontsize=14, loc='upper center', bbox_to_anchor=(0.5, 1.2), ncol=2,
           fancybox=True, edgecolor='black')

# Displaying the grid
plt.grid(True)
plt.tight_layout()

plt.gcf().set_size_inches(8, 6)
# Saving the figure as a PDF with specified size
plt.savefig('counter_latency.pdf', format='pdf', dpi=300, bbox_inches='tight')

# Showing the plot
plt.show()