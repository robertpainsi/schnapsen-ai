# Schnapsen AI

This is a simple approach to train a Neural Network by using a Genetic Algorithm to learn playing [Schnapsen](https://en.wikipedia.org/wiki/Schnapsen).

The current configuration can be found at [GeneticAlgorithm.Config](https://github.com/robertpainsi/schnapsen-ai/blob/main/src/schnapsen/ai/GeneticAlgorithm.java#L39-L43). The program uses 450 neural networks each consisting
of 4 layers (1 input with 72 nodes, 2 hidden with 42 nodes each and 1 output with 20 nodes).
Each neural network plays 42 games against each other neural network to determine the fitter one. The current
state of the game is encoded into the 72 input nodes.

You can test your generated Neural Network by using the Web App found at https://github.com/robertpainsi/schnapsen.
