#!/bin/bash

# --- Configuration ---
# Set the name for the output file
OUTPUT_FILE="collected_files_content.txt"
# Set the starting directory for the search ('.' means current directory)
SEARCH_DIR="."
# Define the file patterns to search for
PATTERNS=(-name '*.java' -o -name '*.xml' -o -name '*.properties')

# --- Script Logic ---

echo "Starting file collection..."
echo "Searching in directory: $SEARCH_DIR"
echo "Looking for file types: .java, .xml, .properties"
echo "Output will be saved to: $OUTPUT_FILE"

# Ensure the output file is empty before starting
> "$OUTPUT_FILE"

# Find the files and process them
# -type f: Find only files
# \( ... \): Group the -name conditions
# -o: OR operator between conditions
# -print0: Print filenames separated by NULL characters (handles special chars)
# while IFS= read -r -d $'\0' file: Read NULL-delimited filenames safely
count=0
find "$SEARCH_DIR" -type f \( "${PATTERNS[@]}" \) -print0 | while IFS= read -r -d $'\0' file; do
    if [ -f "$file" ] && [ -r "$file" ]; then # Check if it's a readable file
        echo "Processing: $file"
        
        # Get the filename from the path using bash parameter expansion
        filename="${file##*/}" 
        
        # Append header and content to the output file
        {
            echo "=================================================="
            echo "File Path: $file"
            echo "Filename : $filename"
            echo "------------------ Content ---------------------"
            cat "$file" # Append the actual file content
            echo # Add a newline for separation after content
            echo "=================================================="
            echo # Add an extra blank line between file entries
        } >> "$OUTPUT_FILE"
        
        count=$((count + 1))
    else
        echo "Skipping non-readable file: $file" >> "$OUTPUT_FILE"
    fi
done

echo "------------------------------------------"
echo "File collection finished."
if [ "$count" -gt 0 ]; then
    echo "$count files were found and their content was collected in '$OUTPUT_FILE'."
else
    echo "No matching files were found in '$SEARCH_DIR'."
    # Optionally remove the empty file if no files were found
    # rm "$OUTPUT_FILE" 
fi

exit 0
