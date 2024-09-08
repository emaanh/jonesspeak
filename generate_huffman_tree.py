import heapq
import pickle

# Define a HuffmanNode class
class HuffmanNode:
    def __init__(self, char, freq, left=None, right=None):
        self.char = char
        self.freq = freq
        self.left = left
        self.right = right

    # Override less than operator to compare nodes based on frequency
    def __lt__(self, other):
        return self.freq < other.freq

# Function to load frequencies from 1m.txt
def load_frequencies(file_path):
    frequency_dict = {}
    with open(file_path, 'r') as file:
        for line in file:
            word, freq = line.split()
            frequency_dict[word] = int(freq)
    return frequency_dict

# Function to create the Huffman tree from a frequency dictionary
def create_huffman_tree(frequency_dict):
    heap = [HuffmanNode(char=w, freq=f) for w, f in frequency_dict.items()]
    heapq.heapify(heap)

    while len(heap) > 1:
        left = heapq.heappop(heap)
        right = heapq.heappop(heap)

        merged = HuffmanNode(char=None, freq=left.freq + right.freq, left=left, right=right)
        heapq.heappush(heap, merged)

    return heap[0]  # The root of the Huffman tree

# Function to generate Huffman codes from a Huffman tree
def huffman_codes(node, prefix="", code_map=None):
    if code_map is None:
        code_map = {}

    if node.char is not None:
        code_map[node.char] = prefix
    else:
        huffman_codes(node.left, prefix + "0", code_map)
        huffman_codes(node.right, prefix + "1", code_map)

    return code_map

# Function to save the Huffman tree and codes to a file
def save_huffman_tree_and_codes(huffman_tree, huffman_codes, tree_file_path, codes_file_path):
    with open(tree_file_path, 'wb') as tree_file:
        pickle.dump(huffman_tree, tree_file)
    with open(codes_file_path, 'wb') as codes_file:
        pickle.dump(huffman_codes, codes_file)


# Main function to generate and save the Huffman tree and codes
def main():
    # Load the frequencies from 1m.txt
    frequency_dict = load_frequencies("1m.txt")

    # Create the Huffman tree
    huffman_tree = create_huffman_tree(frequency_dict)

    # Generate Huffman codes
    codes = huffman_codes(huffman_tree)

    # Save the Huffman tree and codes to files
    save_huffman_tree_and_codes(huffman_tree, codes, "huffman_tree.pkl", "huffman_codes.pkl")

    print("Huffman tree and codes have been successfully saved.")

if __name__ == "__main__":
    main()
