# ikodaCsvLibsvmCreator

ikodaCsvLibsvmCreator collects data into a simple dataframe. 

It saves the output to file in CSV or LIBSVM format. It can also divide the data into a CSV and a LIBSVM component.

In addition, ikodaCsvLibsvmCreator streams the data to a Spark cluster.

### ikodaCsvLibsvmCreator can:

1. Collect data dynamically. New columns create on the fly.
1. Maintain a UID for each column.
1. Maintain a category or label for each row.
1. Run thread safe instances for the synchronous collection of distinct datasets.
1. Save data as a CSV or LIBSVM (either appending or overwriting). 
1. Divide the data for saving into LIBSVM and CSV components.
1. Open Data in CSV ir LIBSVM format.
1. Merge CSV files.
1. Merge LIBSVM files.
1. Stream data to a Spark cluster
