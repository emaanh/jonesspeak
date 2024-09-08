import re
import warnings
import heapq
import pickle
from generate_huffman_tree import huffman_codes
from next_word_prediction import GPT2
import time
import csv

warnings.filterwarnings("ignore", category=FutureWarning)

class HuffmanEncoderDecoder:
    def __init__(self, precomputed_codes_path="huffman_codes.pkl"):
        self.gpt2 = GPT2()
        self.precomputed_codes = self._load_precomputed_codes(precomputed_codes_path)
        self.reversed_precomputed_codes = {"1"+value: key for key, value in self.precomputed_codes.items()}
        self.time = 0
        self.k = 100
        
        
        # with open('dict.csv', 'w') as csv_file:  
        #     writer = csv.writer(csv_file)
        #     for key, value in self.precomputed_codes.items():
        #         writer.writerow([key, value])
            
        self.base_chars = "abcdefghijklmnopqrstuvwxyz"
    
    class HuffmanNode:
        def __init__(self, char, freq, left=None, right=None):
            self.char = char
            self.freq = freq
            self.left = left
            self.right = right

        def __lt__(self, other):
            return self.freq < other.freq

    def _load_precomputed_codes(self, path):
        with open(path, "rb") as codes_file:
            huffman_codes = pickle.load(codes_file)
        return huffman_codes

    def _create_huffman_tree_from_predictions(self, words, probs):
        heap = [self.HuffmanNode(char=w, freq=p) for w, p in zip(words, probs)]
        heapq.heapify(heap)

        while len(heap) > 1:
            left = heapq.heappop(heap)
            right = heapq.heappop(heap)

            merged = self.HuffmanNode(char=None, freq=left.freq + right.freq, left=left, right=right)
            heapq.heappush(heap, merged)

        return heap[0]

    # def combine_codes_with_prefix(self, priority_codes, secondary_codes, priority_prefix="0", secondary_prefix="1"):
    #     combined_codes = {}
    #     for word, code in priority_codes.items():
    #         combined_codes[word] = priority_prefix + code
    #     for word, code in secondary_codes.items():
    #         if word not in combined_codes:
    #             combined_codes[word] = secondary_prefix + code
    #     return combined_codes

    def _encode_word(self, word, prediction_codes, huffman_codes):
        if prediction_codes and word in prediction_codes:
            return "0" + prediction_codes[word]
        elif word in huffman_codes:
            return "1" + huffman_codes[word]
        else:
            raise ValueError("Unrecognized word:", word)

    def _decode_word(self, encoded_word, prediction_codes, reversed_huffman_codes):
        
        if prediction_codes:
            for word, code in prediction_codes.items():
                if (encoded_word).startswith("0"+code):
                    return word, len(code)+1
            
        # for i in range(len(encoded_word), 0, -1):
        for i in range(1, len(encoded_word)+1):
            prefix = encoded_word[:i]
            if prefix in reversed_huffman_codes:
                return reversed_huffman_codes[prefix], len(prefix)

        print("Couldn't find", encoded_word)
        return None, 0
    
    def get_top_prediction_codes(self, context):
        s = time.time()
        top_words, top_probs = self.gpt2.predict_next(context.strip(), k=self.k)
        self.time += (time.time()-s)
        clean_words, clean_probs = self._process_words(top_words, top_probs)
        top_prediction_tree = self._create_huffman_tree_from_predictions(clean_words, clean_probs)
        top_prediction_codes = huffman_codes(top_prediction_tree)
        return top_prediction_codes

    def encode_sentence(self, sentence):
        words = sentence.split()
        encoded_sentence = ""
        context = ""
        count_prediction_trees = 0
        
        for i, word in enumerate(words):
            if i == 0:
                encoded_sentence += self._encode_word(word, None, self.precomputed_codes)
            else:
                top_prediction_codes = self.get_top_prediction_codes(context)
                encoded_word = self._encode_word(word, top_prediction_codes, self.precomputed_codes)
                encoded_sentence += encoded_word
                
                if encoded_word[0] == "0":
                    count_prediction_trees += 1
            
            context += word + " "
        
        # print("NWP prediction rate " + str(round(count_prediction_trees/len(words) * 10000)/100) +"%")

        return encoded_sentence

    def _process_words(self, words, probs):
        processed_words = []
        processed_probs = []
        
        for word, prob in zip(words, probs):
            processed_word = ''.join([char for char in word if char.isalpha()]).lower()
            
            if processed_word:
                processed_words.append(processed_word)
                processed_probs.append(prob)
        
        return processed_words, processed_probs

    def decode_sentence(self, encoded_sentence):
        decoded_sentence = []
        context = ""
        i = 0
        
        while i < len(encoded_sentence):
            top_prediction_codes = None
            if len(decoded_sentence) != 0 and encoded_sentence[i] == "0":
                top_prediction_codes = self.get_top_prediction_codes(context)
            word, length = self._decode_word(encoded_sentence[i:], top_prediction_codes, self.reversed_precomputed_codes)
            if word is None:
                raise ValueError("Decoding failed")
            decoded_sentence.append(word)            
            
            context += word + " "
            i += length

        return " ".join(decoded_sentence)

    def calculate_compression_rate(self, original_text, encoded_text):
        original_size_bits = len(original_text) * 8
        compressed_size_bits = len(encoded_text)
        compression_ratio = original_size_bits / compressed_size_bits
        compression_percentage = (1 - (compressed_size_bits / original_size_bits)) * 100

        return compression_ratio, compression_percentage

    def encode_binary_to_base(self, binary_string):
        if binary_string[0] != '1':
            binary_string = '1' + binary_string
        
        base = len(self.base_chars)
        decimal_value = int(binary_string, 2)
        base_n_string = ''
        while decimal_value > 0:
            remainder = decimal_value % base
            base_n_string = self.base_chars[remainder] + base_n_string
            decimal_value //= base
        
        return base_n_string

    def decode_base_to_binary(self, encoded_string):
        base = len(self.base_chars)
        decimal_value = 0
        for char in encoded_string:
            decimal_value = decimal_value * base + self.base_chars.index(char)
        
        binary_string = bin(decimal_value)[2:]
        binary_string = binary_string.lstrip('0')
        
        # print("OG", binary_string)
        
        return binary_string

    def clean_sentence(self, sentence):
        sentence = sentence.lower()
        cleaned_sentence = re.sub(r'[^a-z\s]', '', sentence).lstrip()
        return cleaned_sentence
    
    def getRatio(self):
        return self.ratio
    def getPercent(self):
        return self.percent


    def encode(self, sentence):
        cleaned_sentence = self.clean_sentence(sentence)
        # print("\n\nCleaned sentence:", cleaned_sentence, "\n")

        encoded_sentence = self.encode_sentence(cleaned_sentence)
        # print("Encoded Sentence:", encoded_sentence, len(encoded_sentence))
        
        encoded_sentence_base = self.encode_binary_to_base(encoded_sentence)
        
        
        compression_ratio, compression_percentage = self.calculate_compression_rate(sentence, encoded_sentence)

        self.ratio = compression_ratio
        self.percent = compression_percentage
        
        return encoded_sentence_base
    
    def decode(self, encoded_sentence_base):
        return self.decode_sentence(self.decode_base_to_binary(encoded_sentence_base))

    def main(self, sentence):
        start = time.time()
        encoded = self.encode(sentence)
        print("encoding time: ", str(time.time()-start))
        print("Encoded Sentence:", encoded)
        print(f"Compression Ratio: {self.getRatio():.2f}")
        print(f"Compression Percentage: {self.getPercent():.2f}%")
        
        start = time.time()
        decoded = self.decode(encoded)
        print("decoding time: ", str(time.time()-start))
        print("Decoded Sentence:", decoded)

    def unitTest(self):
        print("Starting Unit Test")
        input_para = "As the night grew darker, the villagers slowly made their way back to their homes, the echoes of the celebration still lingering in the air. The village returned to its quiet state, with only the sound of crickets and the occasional hoot of an owl breaking the silence. The stars twinkled brightly overhead, watching over the peaceful village as it drifted into a restful slumber. As the day progressed, the villagers gathered in the central square for the annual harvest festival. Tables were laden with freshly picked fruits, baked goods, and hearty stews, all prepared with love and care. Laughter and music filled the air as people danced and celebrated the abundance of the season. The evening sky turned a deep shade of orange, and lanterns were lit, casting a warm glow over the festivities. The village elder shared stories of past generations, weaving tales of courage, wisdom, and tradition. In the tranquil village nestled between rolling hills, the sun bathed the landscape in a warm, golden hue. Children laughed as they played near the clear, sparkling stream, their joy echoing through the air. Birds chirped melodiously from the treetops, adding to the serene atmosphere. The gentle breeze rustled the leaves, carrying the scent of blooming flowers. Old oak trees stood tall, their branches swaying gracefully. Villagers greeted one another with friendly smiles, exchanging pleasantries as they went about their daily tasks. Life in the village was simple, yet filled with contentment."
        input_sentences = input_para.split(".")
        
        for i, sentence in enumerate(input_sentences):
            if sentence:
                # print(self.clean_sentence(sentence))
                # print(self.decode(self.encode(sentence)))
                if self.clean_sentence(sentence)!=self.decode(self.encode(sentence)):
                    return False
            print(f'PASSED: {i+1}/{len(input_sentences)}')
                
        return True

if __name__ == "__main__":
    hed = HuffmanEncoderDecoder()
    # print("Passed all tests" if hed.unitTest() else "Failed tests")    

    sentence = input("Enter text to compress: ")
    s = time.time()
    hed.time = 0
    d = hed.encode(sentence)
    print("encode", time.time()-s)
    print("top", hed.time)
    s = time.time()
    hed.time = 0
    hed.decode(d)
    print("decode", time.time()-s)
    print("top", hed.time)
    # decoded = hed.main(sentence)
    # print("Decoded Sentence:", decoded)
    # hed.main(sentence)
