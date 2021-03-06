/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.tests.junit;


import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Point;
import org.junit.Before;
import org.junit.Test;

/**
 * Automated Test Suite for class org.eclipse.swt.custom.CCombo
 *
 * @see org.eclipse.swt.custom.CCombo
 */
public class Test_org_eclipse_swt_custom_CCombo extends Test_org_eclipse_swt_widgets_Composite {

@Override
@Before
public void setUp() {
	super.setUp();
	ccombo = new CCombo(shell, 0);
	setWidget(ccombo);
}

@Override
@Test
public void test_ConstructorLorg_eclipse_swt_widgets_CompositeI() {
}

@Test
public void test_copy() {
	if (SwtTestUtil.isCocoa) {
		// TODO Fix Cocoa failure.
		if (SwtTestUtil.verbose) {
			System.out
					.println("Excluded test_copy(org.eclipse.swt.tests.junit.Test_org_eclipse_swt_custom_CCombo).");
		}
		return;
	}
	ccombo.setText("123456");
	ccombo.setSelection(new Point(1,3));
	ccombo.copy();
	ccombo.setSelection(new Point(0,0));
	ccombo.paste();
	assertTrue(":a:", ccombo.getText().equals("23123456"));
}

@Test
public void test_cut() {
	if (SwtTestUtil.isCocoa) {
		// TODO Fix Cocoa failure.
		if (SwtTestUtil.verbose) {
			System.out
					.println("Excluded test_cut(org.eclipse.swt.tests.junit.Test_org_eclipse_swt_custom_CCombo).");
		}
		return;
	}
	ccombo.setText("123456");
	ccombo.setSelection(new Point(1,3));
	ccombo.cut();
	assertTrue(":a:", ccombo.getText().equals("1456"));
}

@Override
@Test
public void test_computeSizeIIZ() {
}

@Override
@Test
public void test_getChildren() {
}

@Override
@Test
public void test_isFocusControl() {
	assertTrue(!ccombo.isFocusControl());
}

@Test
public void test_paste() {
	if (SwtTestUtil.isCocoa) {
		// TODO Fix Cocoa failure.
		if (SwtTestUtil.verbose) {
			System.out
					.println("Excluded test_paste(org.eclipse.swt.tests.junit.Test_org_eclipse_swt_custom_CCombo).");
		}
		return;
	}
	ccombo.setText("123456");
	ccombo.setSelection(new Point(1,3));
	ccombo.cut();
	assertTrue(":a:", ccombo.getText().equals("1456"));
	ccombo.paste();
	assertTrue(":a:", ccombo.getText().equals("123456"));
}

@Override
@Test
public void test_redraw() {
}

@Override
@Test
public void test_redrawIIIIZ() {
}

@Override
@Test
public void test_setBackgroundLorg_eclipse_swt_graphics_Color() {
}

@Override
@Test
public void test_setEnabledZ() {
}

@Override
@Test
public void test_setFocus() {
	assertTrue(!ccombo.setFocus());
}

@Override
@Test
public void test_setFontLorg_eclipse_swt_graphics_Font() {
}

@Override
@Test
public void test_setForegroundLorg_eclipse_swt_graphics_Color() {
}

@Override
@Test
public void test_setToolTipTextLjava_lang_String() {
}

@Override
@Test
public void test_setVisibleZ() {
}

/* Custom */
CCombo ccombo;

private void add() {
    ccombo.add("this");
    ccombo.add("is");
    ccombo.add("SWT");
}

@Test
public void test_consistency_MouseSelection () {
    add();
    consistencyPrePackShell();
    consistencyEvent(ccombo.getSize().x-10, 5, 30, ccombo.getItemHeight()*2,
            		 ConsistencyUtility.SELECTION);
}

@Test
public void test_consistency_KeySelection () {
    add();
    consistencyEvent(0, SWT.ARROW_DOWN, 0, 0, ConsistencyUtility.KEY_PRESS);
}

@Test
public void test_consistency_EnterSelection () {
    add();
    consistencyEvent(10, 13, 0, 0, ConsistencyUtility.KEY_PRESS);
}

@Test
public void test_consistency_MenuDetect () {
    add();
    consistencyPrePackShell();
    //on arrow
    consistencyEvent(ccombo.getSize().x-10, 5, 3, 0, ConsistencyUtility.MOUSE_CLICK);
    //on text
    consistencyEvent(10, 5, 3, ConsistencyUtility.ESCAPE_MENU, ConsistencyUtility.MOUSE_CLICK);
}

@Test
public void test_consistency_DragDetect () {
    add();
    consistencyEvent(10, 5, 20, 10, ConsistencyUtility.MOUSE_DRAG);
}

}
