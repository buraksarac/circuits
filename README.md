# circuits
This is a new library I created for validating stream of data using circuits.

Its not finished and I am not sure how it will go:) but you can take a look existing tests.

Some examples:

###Using pair of chars to open/close circuit:

i.e. https://leetcode.com/problems/remove-invalid-parentheses/

```
Circuit<Character> parantheses = Circuits.biCircuit('(', ')').nested();

StringBuilder sb = new StringBuilder();

"()())()".chars().forEach(i -> {
	parantheses.ifAccept((char) i, sb::append);
});

assertTrue("()()()".equals(sb.toString()));
```

###Using multiple pairs

i.e. https://leetcode.com/problems/valid-parentheses/

```
Circuit<Character> circuit = Circuits.multiBiCircuit('(', ')','[', ']','{', '}').nested();
"()[]{}".chars().forEach(i -> {
	circuit.accept((char) i);
});
circuit.assertClosed();

```
###Switching states through same value

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
###Creating when conditions by using another circuit

i.e. test 12.054e1

```		
Circuit<Character> digit = Circuits.between('0', '9').flowing();
Circuit<Character> decimal = Circuits.singlePass('.');
Circuit<Character> exponent = Circuits.singlePass('e');

digit.ignore(decimal, exponent);
digit.when(decimal).expect().circuitOpen();
decimal.when(exponent).expect().circuitOpen();

Circuits<Character> circuits = Circuits.of(digit, decimal, exponent);

"12.0e541".chars().forEach(c->{
	circuits.accept((char) c);
});

```
