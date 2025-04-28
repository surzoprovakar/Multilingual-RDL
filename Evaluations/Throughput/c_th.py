import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd
import numpy as np

# Data from the table
operations = [2000, 4000, 6000, 8000, 10000]

# Latency data (in milliseconds)
latency_data = {
    'Babel CRDT': [363.33, 470, 630, 730, 923.33],
    'Go-CRDT': [760, 1315, 1605, 1955, 2455],
    'Legion': [1445, 1660, 2020, 2595, 3245],
    'T-CRDT': [1125, 1465, 1785, 2360, 3100]
}

# Convert latency to throughput (ops/s)
# throughput_data = {lib: [ops  / (lat / 1000) for ops, lat in zip(operations, latencies)]
#                    for lib, latencies in latency_data.items()}

throughput_data ={'Strategy I (Go-CRDT)': [3.63157, 4.041825, 5.738317, 6.092071, 6.573319], 
                  'Strategy I (Legion)': [2.384083, 2.80963, 3.970297, 4.582851, 5.081664], 
                  'Strategy I (T-CRDT)': [2.777777, 3.230375, 4.061344, 5.089830, 5.525806],
                  'Strategy II (CDF-RDL)': [9.50463, 9.51063, 10.52380, 10.45890, 10.83036]}

# throughput_data ={'Babel CRDT': [1000, 2000, 3000, 4000, 5000], 
#                   'Go-CRDT': [3000, 4000, 5000, 6000, 7000], 
#                   'Legion': [6000, 7000, 8000, 9000, 10000], 
#                   'T-CRDT': [4000, 5000, 6000, 7000, 8000]}
# print(throughput_data)

# Convert to DataFrame for Seaborn
throughput_df = pd.DataFrame(throughput_data, index=operations).melt(var_name='Library', value_name='Throughput')


# Define colors for each library
colors = {
    'Strategy I (Go-CRDT)': 'indianred',
    'Strategy I (Legion)': 'sandybrown',
    'Strategy I (T-CRDT)': 'cornflowerblue',
    'Strategy II (CDF-RDL)': 'cadetblue',
}

# Create the plot
plt.figure(figsize=(8, 5))
ax = sns.boxplot(x='Library', y='Throughput', data=throughput_df, palette=colors, linewidth=1)

# # Customize edge colors if needed
# for artist in ax.artists:
#     # Extract the label from the artist
#     label = throughput_df['Library'].unique()[list(ax.artists).index(artist)]
    
#     # Set edge color for all boxes
#     artist.set_edgecolor('black')
#     artist.set_linewidth(1)

# # Set y-axis ticks at 1000 intervals
# # ax.set_yticks(np.arange(1000, throughput_df['Throughput'].max() + 1000, 1000))

# ax.set_xlabel('')
# plt.grid(True)
# # Set font sizes for ticks and axes
# ax.tick_params(axis='y', labelsize=16)
# ax.tick_params(axis='x', labelsize=10)
# # plt.xlabel('Library', fontsize=20)
# plt.ylabel('Throughput (ops/s)', fontsize=20)
# # plt.title('Throughput Distribution', fontsize=20)

# Remove x-axis labels
ax.set_xticklabels([])
ax.set_xlabel('')

# Customize edge colors
for artist in ax.artists:
    artist.set_edgecolor('black')
    artist.set_linewidth(1)

# Set y-axis ticks
ax.tick_params(axis='y', labelsize=16)
ax.tick_params(axis='x', labelsize=10)
plt.ylabel('Throughput (ops/s)', fontsize=20)
plt.grid(True)

# Create legend manually
handles = [plt.Line2D([0], [0], color=colors[label], lw=10, label=label) for label in colors.keys()]
plt.legend(handles=handles, fontsize=15, loc='upper center', bbox_to_anchor=(0.5, 1.22), ncol=2,
           fancybox=True, edgecolor='black')

# Save figure as PDF
plt.savefig('counter_throughput.pdf', format='pdf', bbox_inches='tight')

# Show plot
plt.show()
