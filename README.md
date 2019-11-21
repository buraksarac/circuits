# circuits
This is a new library I created for validating stream of data using circuits.

Its not finished and I am not sure how it will go:) but you can take a look existing tests.

```
// test 12.054e1
  CircuitCondition<Character> digit = Circuits.between('0', '9');
  CircuitCondition<Character> decimal = Circuits.singlePass('.');
  CircuitCondition<Character> exponent = Circuits.singlePass('e');

  digit.ignore(decimal, exponent);
  digit.when(decimal).expect().circuitOpen();
  decimal.when(exponent).expect().circuitOpen();

  Circuits<Character> circuits = Circuits.of(digit, decimal, exponent);

  char[] chars = "12.0e541".toCharArray();
  for (char c : chars) {
    circuits.accept(c);
  }
```
