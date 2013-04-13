Custom Controls
=========
SComboBox extends JComboBox:
JComboBox with autocomplete search and additional easyToUse methods.

Usage:
Way #1 - From code side:
---------
		SComboBox cmbCustom = new SComboBox();
		cmbCustom.setItems(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"});

Way #2 - From IDE GUI side ( Tested in Netbeans ):
---------
		Open Your Form/Window in Design mode and just drag SComboBox class on it.

Adding new items:
Way #1:
---------
		cmbCustom.addItem("New Item x");
Way #2:
---------
		cmbCustom.addItems( new String[]{"Item 23", "Item 5464", "Item 45222", "Item 3242323"} );
		
Getting text of the selected item:
---------
	String selectedValue = cmbCustom.getText();
	