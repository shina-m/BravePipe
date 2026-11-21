/*
 * SPDX-FileCopyrightText: 2026 BravePipe contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package org.schabi.newpipe.player.helper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.schabi.newpipe.R;

public class PlayerHelperTest {
    private static final String NOTIFICATION_SEEK_KEY = "notification_seek";
    private static final String PLAYER_SEEK_KEY = "player_seek";

    private Context context;
    private SharedPreferences prefs;

    @Before
    public void setUp() {
        context = mock(Context.class);
        prefs = mock(SharedPreferences.class);
        when(context.getString(R.string.notification_prev_next_as_seek_key))
                .thenReturn(NOTIFICATION_SEEK_KEY);
        when(context.getString(R.string.player_prev_next_as_seek_key))
                .thenReturn(PLAYER_SEEK_KEY);
    }

    @Test
    public void notificationPrevNextSeekDefaultsToTrue() {
        when(prefs.getBoolean(eq(NOTIFICATION_SEEK_KEY), anyBoolean())).thenReturn(true);

        assertTrue(PlayerHelper.isNotificationPrevNextAsSeek(context, prefs));
        verify(prefs).getBoolean(NOTIFICATION_SEEK_KEY, true);
    }

    @Test
    public void playerPrevNextSeekDefaultsToFalse() {
        when(prefs.getBoolean(eq(PLAYER_SEEK_KEY), anyBoolean())).thenReturn(false);

        assertFalse(PlayerHelper.isPlayerPrevNextAsSeek(context, prefs));
        verify(prefs).getBoolean(PLAYER_SEEK_KEY, false);
    }

    @Test
    public void playerPrevNextSeekCanBeEnabled() {
        when(prefs.getBoolean(eq(PLAYER_SEEK_KEY), anyBoolean())).thenReturn(true);

        assertTrue(PlayerHelper.isPlayerPrevNextAsSeek(context, prefs));
        verify(prefs).getBoolean(PLAYER_SEEK_KEY, false);
    }
}
