
for i in range(5, 11):
    words = list()
    word_size = i # characters
    with open("words.txt", "r") as f:
        for word in f:
            if len(word) - 1 == word_size:
                words.append(word[:-1])

    print(f"number of {word_size} letter words = ", len(words))
    with open(f"{word_size}_letter_words.csv", "w") as f:
        f.write(",".join(words))
