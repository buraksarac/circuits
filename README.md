# circuits
This is a new library I created for validating stream of data using boolean circuits. 

Its not finished and I am not sure how it will go:) but you can take a look existing tests. 

Some examples:

```


Circuit<Character> biCircuit = Circuits.biCircuit('{', '}').nested();
biCircuit.accept('s');
biCircuit.assertClosed();
biCircuit.accept('.');
biCircuit.assertClosed();
biCircuit.accept('{');// open circuit
biCircuit.assertOpen();
biCircuit.accept('.');// keep open circuit
biCircuit.assertOpen();
biCircuit.accept('{');// open another
biCircuit.assertOpen();
biCircuit.accept('.');// keep open circuit
biCircuit.assertOpen();
biCircuit.accept('}');// close inner
biCircuit.accept('}');// close outer
biCircuit.assertClosed();
biCircuit.accept('a');// should be still closed
biCircuit.assertClosed();
```
		
**Using multiple pairs**

```

Circuit<Character> biCircuit = Circuits.multiBiCircuit('(', ')','[', ']','{', '}').nested();
biCircuit.accept('s');
biCircuit.assertClosed();
biCircuit.accept('.');
biCircuit.assertClosed();
biCircuit.accept('(');// open circuit
biCircuit.assertOpen();
biCircuit.accept('.');// keep open circuit
biCircuit.assertOpen();
biCircuit.accept('[');// open another 
biCircuit.assertOpen();
biCircuit.accept('.');// keep open 
biCircuit.assertOpen();
biCircuit.accept(']');// close inner
biCircuit.assertOpen();
biCircuit.accept(')');// close outer 
biCircuit.assertClosed();
biCircuit.accept('a');// should be still closed
biCircuit.assertClosed();

```

i.e. https://leetcode.com/problems/valid-parentheses/



```
Circuit<Character> circuit = Circuits.multiBiCircuit('(', ')','[', ']','{', '}').nested();
"()[]{}".chars().forEach(i -> {
	circuit.accept((char) i);
});
circuit.assertClosed();

```
**Switching states through same value**

```
Circuit<Character> circuit = Circuits.flipping('.');

circuit.accept('.');// open circuit
assertTrue(circuit.isOpen());
circuit.accept('.');// close circuit
assertTrue(!circuit.isOpen());
circuit.accept(null);// close circuit
assertTrue(!circuit.isOpen());
circuit.accept('.');// open circuit
assertTrue(circuit.isOpen());

```
**Creating when conditions by using another circuit**

i.e. test 12.054e1

```		
Circuit<Character> digit = Circuits.between('0', '9').flowing();
Circuit<Character> decimal = Circuits.singlePass('.');
Circuit<Character> exponent = Circuits.singlePass('e');

digit
	.when(decimal).expect().circuitOpen()
	.and().when(exponent).expect().circuitOpen()
	.and().when(exponent).expect().open(decimal);


Circuits<Character> circuits = Circuits.of(digit, decimal, exponent);

"12.0e541".chars().forEach(c->{
	circuits.accept((char) c);
});

```
***Any occurence by count***

```
Circuit<Character> circuit = Circuits.<Character>any(5);
circuit.assertClosed();
circuit.accept('a'); // 1
circuit.assertOpen();
circuit.accept('1'); // 2
circuit.assertOpen();
circuit.accept('/'); // 3
circuit.assertOpen();
circuit.accept(':'); // 4
circuit.assertOpen();
circuit.accept('}'); // 5
circuit.assertClosed();
circuit.accept('|');
circuit.assertOpen();
```
