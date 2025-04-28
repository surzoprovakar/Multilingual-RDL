import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd
import numpy as np

# Data from the table
operations = [2000, 4000, 6000, 8000, 10000]

# Latency data (in milliseconds)
latency_data = {
    'Babel CRDT': [480,
620,
796.6666667,
993.3333333,
1280],
    'Go-CRDT': [920,
1485,
1780,
2315,
3135],
    'Legion': [1685,
1960,
2375,
3140,
4225],
    'T-CRDT': [1400,
1840,
2200,
2775,
3545]
}

# Convert latency to throughput (ops/s)
# throughput_data = {lib: [ops / (lat / 1000) for ops, lat in zip(operations, latencies)]
#                    for lib, latencies in latency_data.items()}

# print(throughput_data)

throughput_data={'Strategy I (Go-CRDT)': [3.17391, 3.89360, 4.17078, 4.85572, 5.18979], 
                'Strategy I (Legion)': [1.98694, 2.44081, 3.32631, 3.54777, 4.16686], 
                'Strategy I (T-CRDT)': [2.42857, 3.17391, 3.72727, 4.18288, 4.82087],
                'Strategy II (CDF-RDL)': [6.86667, 7.15161, 7.43138, 7.65369, 7.8125]}

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
# ax.set_yticks(np.arange(1000, throughput_df['Throughput'].max() + 1000, 1000))

# ax.set_xlabel('')
# plt.grid(True)
# # Set font sizes for ticks and axes
# ax.tick_params(axis='y', labelsize=16)
# ax.tick_params(axis='x', labelsize=18)
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
plt.savefig('map_throughput.pdf', format='pdf', bbox_inches='tight')

# Show plot
plt.show()
