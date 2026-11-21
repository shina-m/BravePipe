/*
 * SPDX-FileCopyrightText: 2026 BravePipe contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package org.schabi.newpipe.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

public class SettingsDefaultsTest {
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    private static Document parse(final String relativePath) throws Exception {
        Path path = Path.of(relativePath);
        if (!Files.exists(path)) {
            path = Path.of("app", relativePath);
        }
        assertTrue("Missing XML file: " + path, Files.exists(path));

        try (InputStream input = Files.newInputStream(path)) {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            return factory.newDocumentBuilder().parse(input);
        }
    }

    private static Map<String, String> defaults(final Document document, final String tagName) {
        final Map<String, String> result = new HashMap<>();
        final NodeList nodes = document.getElementsByTagName(tagName);
        for (int i = 0; i < nodes.getLength(); i++) {
            final Element element = (Element) nodes.item(i);
            final String key = element.getAttributeNS(ANDROID_NS, "key");
            if (!key.isEmpty()) {
                result.put(key, element.getAttributeNS(ANDROID_NS, "defaultValue"));
            }
        }
        return result;
    }

    private static String anchor(final Document document, final String id, final String attribute) {
        final NodeList nodes = document.getElementsByTagName("*");
        for (int i = 0; i < nodes.getLength(); i++) {
            final Node node = nodes.item(i);
            if (node instanceof Element) {
                final Element element = (Element) node;
                if (id.equals(element.getAttributeNS(ANDROID_NS, "id"))) {
                    return element.getAttributeNS(ANDROID_NS, attribute);
                }
            }
        }
        return "";
    }

    @Test
    public void notificationSettingsUseNewDefaults() throws Exception {
        final Map<String, String> defaults = defaults(
                parse("src/main/res/xml/player_notification_settings.xml"),
                "SwitchPreferenceCompat");

        assertEquals("false", defaults.get("@string/media_session_hide_prev_next_key"));
        assertEquals("true", defaults.get("@string/notification_prev_next_as_seek_key"));
        assertEquals("false", defaults.get("@string/player_prev_next_as_seek_key"));
    }

    @Test
    public void inAppButtonsDefaultToNothing() throws Exception {
        final Map<String, String> defaults = defaults(
                parse("src/main/res/xml/video_audio_settings.xml"),
                "ListPreference");

        assertEquals("@string/player_button_action_nothing",
                defaults.get("@string/player_button_left_outer_key"));
        assertEquals("@string/player_button_action_nothing",
                defaults.get("@string/player_button_left_inner_key"));
        assertEquals("@string/player_button_action_nothing",
                defaults.get("@string/player_button_right_inner_key"));
        assertEquals("@string/player_button_action_nothing",
                defaults.get("@string/player_button_right_outer_key"));
    }

    @Test
    public void mainPlayerPutsSeekButtonsNextToPlayPause() throws Exception {
        final Document document = parse("src/main/res/layout/player.xml");
        final Set<String> wanted = Set.of(
                "@+id/playerButtonLeftOuter",
                "@+id/playerButtonLeftInner",
                "@+id/playPauseButton",
                "@+id/playerButtonRightInner",
                "@+id/playerButtonRightOuter");

        final List<String> actual = new ArrayList<>();
        final NodeList nodes = document.getElementsByTagName(
                "androidx.appcompat.widget.AppCompatImageButton");
        for (int i = 0; i < nodes.getLength(); i++) {
            final String id = ((Element) nodes.item(i)).getAttributeNS(ANDROID_NS, "id");
            if (wanted.contains(id)) {
                actual.add(id);
            }
        }

        assertEquals(List.of(
                "@+id/playerButtonLeftOuter",
                "@+id/playerButtonLeftInner",
                "@+id/playPauseButton",
                "@+id/playerButtonRightInner",
                "@+id/playerButtonRightOuter"), actual);
    }

    @Test
    public void queueControlsPutSeekButtonsNextToPlayPause() throws Exception {
        final Document document = parse("src/main/res/layout/activity_player_queue_control.xml");

        assertEquals("@+id/control_backward",
                anchor(document, "@+id/control_repeat", "layout_toLeftOf"));
        assertEquals("@+id/control_fast_rewind",
                anchor(document, "@+id/control_backward", "layout_toLeftOf"));
        assertEquals("@id/control_play_pause",
                anchor(document, "@+id/control_fast_rewind", "layout_toLeftOf"));
        assertEquals("@id/control_play_pause",
                anchor(document, "@+id/control_fast_forward", "layout_toRightOf"));
        assertEquals("@+id/control_fast_forward",
                anchor(document, "@+id/control_forward", "layout_toRightOf"));
        assertEquals("@+id/control_forward",
                anchor(document, "@+id/control_shuffle", "layout_toRightOf"));
    }
}
