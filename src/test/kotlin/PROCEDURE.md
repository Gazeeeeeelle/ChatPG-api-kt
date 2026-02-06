# Procedure for Testing

-------
## For Unit

### The tester addresses:
* Solely the unit's intent shown to each of the cases in the set of relevant stimuli, 
i.e. its delegations, its returned value or exception thrown.

### Remarks:
* If the delegated methods of the unit works is not its responsibility and therefore musnt't be addressed.
* Such statement implies on a *Testing Cascade*, in which for a unit to be truly tested all the
abstraction levels below it must have been tested as well.
* The unit is only truly tested under entire fulfillment of the *Testing Cascade* it requires.
* External libraries and language resources are assumed to be functional and do not require tests.
