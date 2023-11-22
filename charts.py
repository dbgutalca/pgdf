import matplotlib.pyplot as plt

# Sample data
graphs = ['G1', 'G2', 'G3', 'G4', 'G5', 'G6']
formats = ['PGDF', 'YARS-PG', 'GraphML', 'JSON']

file_sizes = {
    'G1': [80.0706, 117.0532, 285.9697, 382.4147],
    'G2': [242.8747, 353.0723, 865.6739, 1167.7353],
    'G3': [918.0595, 1323.0217, 3233.1688, 4375.9563],
    'G4': [2804.7791, 4021.5572, 9826.3001, 13326.9722],
    'G5': [9380.8274, 13404.7457, 32812.3449, 44574.4658],
    'G6': [29228.5727, 41420.3568, 100739.9361, 136818.0497]
}

times = {
    'G1': [0.2827948213, 0.3897512629, 0.5017428455, 0.5035352906],
    'G2': [0.5258868978, 0.9185312355, 1.2763582723, 1.075940247],
    'G3': [1.3731453243, 3.0898357212, 4.543915302, 3.7508602706],
    'G4': [3.8154995732, 9.3056054584, 12.7239872266, 10.2450672994],
    'G5': [12.57428891894, 31.4881417303, 45.6313625583, 36.974055727],
    'G6': [37.5532872644, 90.5498767142, 132.1138155409, 108.2834835977]
}

# Set up the bar chart
fig, ax = plt.subplots()

# Width of the bars
bar_width = 0.2

# Set the positions for the bars
positions = range(len(graphs))

colors = ['#A6CEE3', '#1F78B4', '#B2DF8A', '#33A02C']

# Plot the bars for each format
for i, format_name in enumerate(formats):
    sizes = [times[graph][i] for graph in graphs]
    ax.bar(
        [pos + i * bar_width for pos in positions],
        sizes,
        width=bar_width,
        label=format_name,
        color=colors[i]
    )

# Set labels and title
ax.set_xlabel('Graphs')
ax.set_ylabel('Execution Time (sec)')
ax.set_yscale('log')
#ax.set_title('File Size for Each Graph and Format')
ax.set_xticks([pos + (len(formats) - 1) * bar_width / 2 for pos in positions])
ax.set_xticklabels(graphs)
ax.legend()

# Show the plot
plt.show()
