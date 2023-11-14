#!/bin/bash

# Run the script for each specified value
for value in 0.1 1 10 100; do
    echo ${value}
    # Run the script
    ./your_script.sh "${value}"
    
    # Run jar for each file type
    for file_type in pgdf yarspg graphml graphson json; do
        java -jar your_jar_file.jar /path/to/json "--${file_type}"
    done
    
    # Delete folder
    rm -rf "$folder"
done
