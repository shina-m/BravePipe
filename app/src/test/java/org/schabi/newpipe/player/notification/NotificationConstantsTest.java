/*
 * SPDX-FileCopyrightText: 2026 BravePipe contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package org.schabi.newpipe.player.notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.schabi.newpipe.R;

import java.util.List;

public class NotificationConstantsTest {
    private static final String HIDE_KEY = "hide_prev_next";
    private static final int SDK_PRE_TIRAMISU = Build.VERSION_CODES.S_V2;
    private static final int SDK_TIRAMISU = Build.VERSION_CODES.TIRAMISU;

    private Context context;
    private SharedPreferences prefs;

    @Before
    public void setUp() {
        context = mock(Context.class);
        prefs = mock(SharedPreferences.class);
        when(context.getString(R.string.media_session_hide_prev_next_key)).thenReturn(HIDE_KEY);
    }

    @Test
    public void hidePrevNextDefaultsToFalse() {
        when(prefs.getBoolean(eq(HIDE_KEY), anyBoolean())).thenReturn(false);

        assertFalse(NotificationConstants.isHidePrevNext(context, prefs));
        verify(prefs).getBoolean(HIDE_KEY, false);
    }

    @Test
    public void hiddenPrevNextUsesRewindPlayForwardDefaults() {
        when(prefs.getBoolean(eq(HIDE_KEY), anyBoolean())).thenReturn(true);

        assertEquals(NotificationConstants.NOTHING,
                NotificationConstants.getDefaultAction(context, prefs, 0, SDK_TIRAMISU));
        assertEquals(NotificationConstants.REWIND,
                NotificationConstants.getDefaultAction(context, prefs, 1, SDK_TIRAMISU));
        assertEquals(NotificationConstants.PLAY_PAUSE_BUFFERING,
                NotificationConstants.getDefaultAction(context, prefs, 2, SDK_TIRAMISU));
        assertEquals(NotificationConstants.FORWARD,
                NotificationConstants.getDefaultAction(context, prefs, 3, SDK_TIRAMISU));
        assertEquals(NotificationConstants.NOTHING,
                NotificationConstants.getDefaultAction(context, prefs, 4, SDK_TIRAMISU));
    }

    @Test
    public void visiblePrevNextOnAndroid13HasNoCustomOuterActionsByDefault() {
        when(prefs.getBoolean(eq(HIDE_KEY), anyBoolean())).thenReturn(false);

        assertEquals(NotificationConstants.PREVIOUS,
                NotificationConstants.getDefaultAction(context, prefs, 0, SDK_TIRAMISU));
        assertEquals(NotificationConstants.NOTHING,
                NotificationConstants.getDefaultAction(context, prefs, 1, SDK_TIRAMISU));
        assertEquals(NotificationConstants.PLAY_PAUSE_BUFFERING,
                NotificationConstants.getDefaultAction(context, prefs, 2, SDK_TIRAMISU));
        assertEquals(NotificationConstants.NOTHING,
                NotificationConstants.getDefaultAction(context, prefs, 3, SDK_TIRAMISU));
        assertEquals(NotificationConstants.NOTHING,
                NotificationConstants.getDefaultAction(context, prefs, 4, SDK_TIRAMISU));
    }

    @Test
    public void visiblePrevNextBeforeAndroid13UsesPrevPlayNextDefaults() {
        when(prefs.getBoolean(eq(HIDE_KEY), anyBoolean())).thenReturn(false);

        assertEquals(NotificationConstants.PREVIOUS,
                NotificationConstants.getDefaultAction(context, prefs, 0, SDK_PRE_TIRAMISU));
        assertEquals(NotificationConstants.NOTHING,
                NotificationConstants.getDefaultAction(context, prefs, 1, SDK_PRE_TIRAMISU));
        assertEquals(NotificationConstants.PLAY_PAUSE_BUFFERING,
                NotificationConstants.getDefaultAction(context, prefs, 2, SDK_PRE_TIRAMISU));
        assertEquals(NotificationConstants.NOTHING,
                NotificationConstants.getDefaultAction(context, prefs, 3, SDK_PRE_TIRAMISU));
        assertEquals(NotificationConstants.NEXT,
                NotificationConstants.getDefaultAction(context, prefs, 4, SDK_PRE_TIRAMISU));
    }

    @Test
    public void compactDefaultsKeepPrevPlayNextBeforeAndroid13() {
        assertEquals(List.of(0, 2, 4), NotificationConstants.SLOT_COMPACT_DEFAULTS);
    }
}
