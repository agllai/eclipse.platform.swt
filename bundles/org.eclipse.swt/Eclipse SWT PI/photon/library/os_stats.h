/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/

#ifdef NATIVE_STATS
extern int OS_nativeFunctionCount;
extern int OS_nativeFunctionCallCount[];
extern char* OS_nativeFunctionNames[];
#define OS_NATIVE_ENTER(env, that, func) OS_nativeFunctionCallCount[func]++;
#define OS_NATIVE_EXIT(env, that, func) 
#else
#ifndef OS_NATIVE_ENTER
#define OS_NATIVE_ENTER(env, that, func) 
#endif
#ifndef OS_NATIVE_EXIT
#define OS_NATIVE_EXIT(env, that, func) 
#endif
#endif

typedef enum {
	PfDecomposeStemToID_FUNC,
	PfExtentText__Lorg_eclipse_swt_internal_photon_PhRect_1t_2Lorg_eclipse_swt_internal_photon_PhPoint_1t_2III_FUNC,
	PfExtentText__Lorg_eclipse_swt_internal_photon_PhRect_1t_2Lorg_eclipse_swt_internal_photon_PhPoint_1t_2_3B_3BI_FUNC,
	PfExtentWideText_FUNC,
	PfFontDescription_FUNC,
	PfFontFlags_FUNC,
	PfFontSize_FUNC,
	PfFreeFont_FUNC,
	PfGenerateFontName_FUNC,
	PfLoadMetrics_FUNC,
	PfQueryFontInfo_FUNC,
	PfQueryFonts_FUNC,
	PgAlphaOff_FUNC,
	PgAlphaOn_FUNC,
	PgCreateGC_FUNC,
	PgDestroyGC_FUNC,
	PgDrawArc_FUNC,
	PgDrawArrow_FUNC,
	PgDrawBitmap_FUNC,
	PgDrawEllipse_FUNC,
	PgDrawGradient_FUNC,
	PgDrawILine_FUNC,
	PgDrawIPixel_FUNC,
	PgDrawIRect_FUNC,
	PgDrawImage_FUNC,
	PgDrawMultiTextArea_FUNC,
	PgDrawPhImageRectmx_FUNC,
	PgDrawPolygon_FUNC,
	PgDrawRoundRect_FUNC,
	PgDrawTImage_FUNC,
	PgDrawText_FUNC,
	PgExtentMultiText_FUNC,
	PgFlush_FUNC,
	PgGetVideoMode_FUNC,
	PgGetVideoModeInfo_FUNC,
	PgReadScreen_FUNC,
	PgReadScreenSize_FUNC,
	PgSetAlpha_FUNC,
	PgSetClipping_FUNC,
	PgSetDrawBufferSize_FUNC,
	PgSetDrawMode_FUNC,
	PgSetFillColor_FUNC,
	PgSetFillTransPat_FUNC,
	PgSetFont_FUNC,
	PgSetGC_FUNC,
	PgSetMultiClip_FUNC,
	PgSetPalette_FUNC,
	PgSetRegion_FUNC,
	PgSetStrokeCap_FUNC,
	PgSetStrokeColor_FUNC,
	PgSetStrokeDash_FUNC,
	PgSetStrokeJoin_FUNC,
	PgSetStrokeWidth_FUNC,
	PgSetTextColor_FUNC,
	PgSetTextXORColor_FUNC,
	PgSetUserClip_FUNC,
	PgShmemCreate_FUNC,
	PgShmemDestroy_FUNC,
	PhAddMergeTiles_FUNC,
	PhAreaToRect_FUNC,
	PhBlit_FUNC,
	PhClipTilings_FUNC,
	PhClipboardCopy_FUNC,
	PhClipboardCopyString_FUNC,
	PhClipboardPasteFinish_FUNC,
	PhClipboardPasteStart_FUNC,
	PhClipboardPasteString_FUNC,
	PhClipboardPasteType_FUNC,
	PhClipboardPasteTypeN_FUNC,
	PhCoalesceTiles_FUNC,
	PhCopyTiles_FUNC,
	PhCreateImage_FUNC,
	PhDCSetCurrent_FUNC,
	PhDeTranslateTiles_FUNC,
	PhEventNext_FUNC,
	PhEventPeek_FUNC,
	PhFreeTiles_FUNC,
	PhGetData_FUNC,
	PhGetMsgSize_FUNC,
	PhGetRects_FUNC,
	PhGetTile_FUNC,
	PhInitDrag_FUNC,
	PhInputGroup_FUNC,
	PhIntersectTilings_FUNC,
	PhKeyToMb_FUNC,
	PhMakeGhostBitmap_FUNC,
	PhMakeTransBitmap_FUNC,
	PhMergeTiles_FUNC,
	PhMoveCursorAbs_FUNC,
	PhQueryCursor_FUNC,
	PhQueryRids_FUNC,
	PhRectIntersect_FUNC,
	PhRectUnion__II_FUNC,
	PhRectUnion__Lorg_eclipse_swt_internal_photon_PhRect_1t_2Lorg_eclipse_swt_internal_photon_PhRect_1t_2_FUNC,
	PhRectsToTiles_FUNC,
	PhRegionQuery_FUNC,
	PhReleaseImage_FUNC,
	PhSortTiles_FUNC,
	PhTilesToRects_FUNC,
	PhTranslateTiles_FUNC,
	PhWindowQueryVisible_FUNC,
	PiCropImage_FUNC,
	PiDuplicateImage_FUNC,
	PmMemCreateMC_FUNC,
	PmMemFlush_FUNC,
	PmMemReleaseMC_FUNC,
	PmMemStart_FUNC,
	PmMemStop_FUNC,
	PtAddCallback_FUNC,
	PtAddEventHandler_FUNC,
	PtAddFilterCallback_FUNC,
	PtAddHotkeyHandler_FUNC,
	PtAlert_FUNC,
	PtAppAddInput_FUNC,
	PtAppAddWorkProc_FUNC,
	PtAppCreatePulse_FUNC,
	PtAppDeletePulse_FUNC,
	PtAppProcessEvent_FUNC,
	PtAppPulseTrigger_FUNC,
	PtAppRemoveInput_FUNC,
	PtAppRemoveWorkProc_FUNC,
	PtBeep_FUNC,
	PtBlit_FUNC,
	PtBlockAllWindows_FUNC,
	PtBlockWindow_FUNC,
	PtButton_FUNC,
	PtCalcBorder_FUNC,
	PtCalcCanvas_FUNC,
	PtClippedBlit_FUNC,
	PtColorSelect_FUNC,
	PtComboBox_FUNC,
	PtContainer_FUNC,
	PtContainerFindFocus_FUNC,
	PtContainerFocusNext_FUNC,
	PtContainerFocusPrev_FUNC,
	PtContainerGiveFocus_FUNC,
	PtContainerHold_FUNC,
	PtContainerRelease_FUNC,
	PtCreateAppContext_FUNC,
	PtCreateWidget_FUNC,
	PtCreateWidgetClass_FUNC,
	PtDamageExtent_FUNC,
	PtDamageWidget_FUNC,
	PtDestroyWidget_FUNC,
	PtDisjoint_FUNC,
	PtEnter_FUNC,
	PtEventHandler_FUNC,
	PtExtentWidget_FUNC,
	PtExtentWidgetFamily_FUNC,
	PtFileSelection_FUNC,
	PtFindDisjoint_FUNC,
	PtFlush_FUNC,
	PtFontSelection_FUNC,
	PtForwardWindowEvent_FUNC,
	PtFrameSize_FUNC,
	PtGetAbsPosition_FUNC,
	PtGetResources_FUNC,
	PtGlobalFocusNext_FUNC,
	PtGlobalFocusNextContainer_FUNC,
	PtGlobalFocusPrev_FUNC,
	PtGlobalFocusPrevContainer_FUNC,
	PtGroup_FUNC,
	PtHit_FUNC,
	PtHold_FUNC,
	PtInflateBalloon_FUNC,
	PtInit_FUNC,
	PtIsFocused_FUNC,
	PtLabel_FUNC,
	PtLeave_FUNC,
	PtList_FUNC,
	PtListAddItems_FUNC,
	PtListDeleteAllItems_FUNC,
	PtListDeleteItemPos_FUNC,
	PtListGotoPos_FUNC,
	PtListItemPos_FUNC,
	PtListReplaceItemPos_FUNC,
	PtListSelectPos_FUNC,
	PtListUnselectPos_FUNC,
	PtMainLoop_FUNC,
	PtMenu_FUNC,
	PtMenuBar_FUNC,
	PtMenuButton_FUNC,
	PtMultiText_FUNC,
	PtNextTopLevelWidget_FUNC,
	PtNumericInteger_FUNC,
	PtPane_FUNC,
	PtPanelGroup_FUNC,
	PtPositionMenu_FUNC,
	PtProgress_FUNC,
	PtReParentWidget_FUNC,
	PtRealizeWidget_FUNC,
	PtRegion_FUNC,
	PtRelease_FUNC,
	PtRemoveCallback_FUNC,
	PtRemoveHotkeyHandler_FUNC,
	PtScrollArea_FUNC,
	PtScrollContainer_FUNC,
	PtScrollbar_FUNC,
	PtSendEventToWidget_FUNC,
	PtSeparator_FUNC,
	PtSetAreaFromWidgetCanvas_FUNC,
	PtSetParentWidget_FUNC,
	PtSetResource_FUNC,
	PtSetResources_FUNC,
	PtSlider_FUNC,
	PtSuperClassDraw_FUNC,
	PtSyncWidget_FUNC,
	PtText_FUNC,
	PtTextGetSelection_FUNC,
	PtTextModifyText__IIIIII_FUNC,
	PtTextModifyText__IIII_3BI_FUNC,
	PtTextSetSelection_FUNC,
	PtTimer_FUNC,
	PtToggleButton_FUNC,
	PtToolbar_FUNC,
	PtUnblockWindows_FUNC,
	PtUnrealizeWidget_FUNC,
	PtValidParent_FUNC,
	PtWebClient_FUNC,
	PtWidgetArea_FUNC,
	PtWidgetBrotherBehind_FUNC,
	PtWidgetBrotherInFront_FUNC,
	PtWidgetCanvas__II_FUNC,
	PtWidgetCanvas__ILorg_eclipse_swt_internal_photon_PhRect_1t_2_FUNC,
	PtWidgetChildBack_FUNC,
	PtWidgetChildFront_FUNC,
	PtWidgetClass_FUNC,
	PtWidgetExtent__II_FUNC,
	PtWidgetExtent__ILorg_eclipse_swt_internal_photon_PhRect_1t_2_FUNC,
	PtWidgetFlags_FUNC,
	PtWidgetInsert_FUNC,
	PtWidgetIsClassMember_FUNC,
	PtWidgetIsRealized_FUNC,
	PtWidgetOffset_FUNC,
	PtWidgetParent_FUNC,
	PtWidgetPreferredSize_FUNC,
	PtWidgetRid_FUNC,
	PtWidgetToBack_FUNC,
	PtWidgetToFront_FUNC,
	PtWindow_FUNC,
	PtWindowFocus_FUNC,
	PtWindowGetState_FUNC,
	PtWindowToBack_FUNC,
	PtWindowToFront_FUNC,
	memmove__ILorg_eclipse_swt_internal_photon_PgAlpha_1t_2I_FUNC,
	memmove__ILorg_eclipse_swt_internal_photon_PhArea_1t_2I_FUNC,
	memmove__ILorg_eclipse_swt_internal_photon_PhCursorDef_1t_2I_FUNC,
	memmove__ILorg_eclipse_swt_internal_photon_PhEvent_1t_2I_FUNC,
	memmove__ILorg_eclipse_swt_internal_photon_PhImage_1t_2I_FUNC,
	memmove__ILorg_eclipse_swt_internal_photon_PhPoint_1t_2I_FUNC,
	memmove__ILorg_eclipse_swt_internal_photon_PhPointerEvent_1t_2I_FUNC,
	memmove__ILorg_eclipse_swt_internal_photon_PhRect_1t_2I_FUNC,
	memmove__ILorg_eclipse_swt_internal_photon_PhTile_1t_2I_FUNC,
	memmove__ILorg_eclipse_swt_internal_photon_PtTextCallback_1t_2I_FUNC,
	memmove__ILorg_eclipse_swt_internal_photon_PtWebClient2Data_1t_2I_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_FontDetails_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PgAlpha_1t_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PgMap_1t_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PhClipHeader_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PhEvent_1t_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PhImage_1t_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PhKeyEvent_1t_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PhPointerEvent_1t_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PhRect_1t_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PhTile_1t_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PhWindowEvent_1t_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PtCallbackInfo_1t_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PtScrollbarCallback_1t_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PtTextCallback_1t_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PtWebDataReqCallback_1t_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PtWebMetaDataCallback_1t_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PtWebStatusCallback_1t_2II_FUNC,
	memmove__Lorg_eclipse_swt_internal_photon_PtWebWindowCallback_1t_2II_FUNC,
	memmove___3BLorg_eclipse_swt_internal_photon_PhClipHeader_2I_FUNC,
	strdup_FUNC,
	uname_FUNC,
} OS_FUNCS;
