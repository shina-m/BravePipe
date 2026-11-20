package org.schabi.newpipe.settings.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.widget.TextViewCompat;

import org.schabi.newpipe.R;
import org.schabi.newpipe.databinding.ListRadioIconItemBinding;
import org.schabi.newpipe.databinding.SingleChoiceDialogViewBinding;
import org.schabi.newpipe.player.notification.NotificationConstants;
import org.schabi.newpipe.util.DeviceUtils;
import org.schabi.newpipe.util.ThemeHelper;
import org.schabi.newpipe.views.FocusOverlayView;

import java.util.Objects;
import java.util.function.BiConsumer;

class NotificationSlot {

    private static final int[] SLOT_ITEMS = {
            R.id.notificationAction0,
            R.id.notificationAction1,
            R.id.notificationAction2,
            R.id.notificationAction3,
            R.id.notificationAction4,
    };

    private static final int[] SLOT_TITLES = {
            R.string.notification_action_0_title,
            R.string.notification_action_1_title,
            R.string.notification_action_2_title,
            R.string.notification_action_3_title,
            R.string.notification_action_4_title,
    };

    private final int i;
    private @NotificationConstants.Action int selectedAction;
    private final Context context;
    private final BiConsumer<Integer, CheckBox> onToggleCompactSlot;

    private ImageView icon;
    private TextView summary;

    NotificationSlot(final Context context,
                     final SharedPreferences prefs,
                     final int actionIndex,
                     final View parentView,
                     final boolean isCompactSlotChecked,
                     final BiConsumer<Integer, CheckBox> onToggleCompactSlot) {
        this.context = context;
        this.i = actionIndex;
        this.onToggleCompactSlot = onToggleCompactSlot;

        final boolean hidePrevNext = NotificationConstants.isHidePrevNext(context, prefs);
        selectedAction = Objects.requireNonNull(prefs).getInt(
                context.getString(NotificationConstants.SLOT_PREF_KEYS[i]),
                NotificationConstants.getDefaultAction(context, prefs, i));
        final View view = parentView.findViewById(SLOT_ITEMS[i]);

        final boolean shouldShow;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            shouldShow = true;
        } else if (hidePrevNext) {
            shouldShow = (i != 2);
        } else {
            shouldShow = (i >= 3);
        }

        if (shouldShow) {
            view.setVisibility(View.VISIBLE);
            setupSelectedAction(view);
            setupTitle(view, hidePrevNext);
            setupCheckbox(view, isCompactSlotChecked);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    void setupTitle(final View view, final boolean hidePrevNext) {
        final int titleRes;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (hidePrevNext) {
                switch (i) {
                    case 0:
                        titleRes = R.string.notification_action_0_title_4slot;
                        break;
                    case 1:
                        titleRes = R.string.notification_action_1_title_4slot;
                        break;
                    case 3:
                        titleRes = R.string.notification_action_3_title_4slot;
                        break;
                    case 4:
                        titleRes = R.string.notification_action_4_title_4slot;
                        break;
                    default:
                        titleRes = SLOT_TITLES[i];
                        break;
                }
            } else {
                switch (i) {
                    case 3:
                        titleRes = R.string.notification_action_3_title_2slot;
                        break;
                    case 4:
                        titleRes = R.string.notification_action_4_title_2slot;
                        break;
                    default:
                        titleRes = SLOT_TITLES[i];
                        break;
                }
            }
        } else {
            titleRes = SLOT_TITLES[i];
        }

        ((TextView) view.findViewById(R.id.notificationActionTitle))
                .setText(titleRes);
        view.findViewById(R.id.notificationActionClickableArea).setOnClickListener(
                v -> openActionChooserDialog());
    }

    void setupCheckbox(final View view, final boolean isCompactSlotChecked) {
        final CheckBox compactSlotCheckBox = view.findViewById(R.id.notificationActionCheckBox);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // there are no compact slots to customize on Android 13+
            compactSlotCheckBox.setVisibility(View.GONE);
            view.findViewById(R.id.notificationActionCheckBoxClickableArea)
                    .setVisibility(View.GONE);
            return;
        }

        compactSlotCheckBox.setChecked(isCompactSlotChecked);
        view.findViewById(R.id.notificationActionCheckBoxClickableArea).setOnClickListener(
                v -> onToggleCompactSlot.accept(i, compactSlotCheckBox));
    }

    void setupSelectedAction(final View view) {
        icon = view.findViewById(R.id.notificationActionIcon);
        summary = view.findViewById(R.id.notificationActionSummary);
        updateInfo();
    }

    void updateInfo() {
        if (NotificationConstants.ACTION_ICONS[selectedAction] == 0) {
            icon.setImageDrawable(null);
        } else {
            icon.setImageDrawable(AppCompatResources.getDrawable(context,
                    NotificationConstants.ACTION_ICONS[selectedAction]));
        }

        summary.setText(NotificationConstants.getActionName(context, selectedAction));
    }

    void openActionChooserDialog() {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final SingleChoiceDialogViewBinding binding =
                SingleChoiceDialogViewBinding.inflate(inflater);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(SLOT_TITLES[i])
                .setView(binding.getRoot())
                .setCancelable(true)
                .create();

        final View.OnClickListener radioButtonsClickListener = v -> {
            selectedAction = NotificationConstants.ALL_ACTIONS[v.getId()];
            updateInfo();
            alertDialog.dismiss();
        };

        for (int id = 0; id < NotificationConstants.ALL_ACTIONS.length; ++id) {
            final int action = NotificationConstants.ALL_ACTIONS[id];
            final RadioButton radioButton = ListRadioIconItemBinding.inflate(inflater)
                    .getRoot();

            // if present set action icon with correct color
            final int iconId = NotificationConstants.ACTION_ICONS[action];
            if (iconId != 0) {
                radioButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, iconId, 0);

                final var color = ColorStateList.valueOf(ThemeHelper
                        .resolveColorFromAttr(context, android.R.attr.textColorPrimary));
                TextViewCompat.setCompoundDrawableTintList(radioButton, color);
            }

            radioButton.setText(NotificationConstants.getActionName(context, action));
            radioButton.setChecked(action == selectedAction);
            radioButton.setId(id);
            radioButton.setLayoutParams(new RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            radioButton.setOnClickListener(radioButtonsClickListener);
            binding.list.addView(radioButton);
        }
        alertDialog.show();

        if (DeviceUtils.isTv(context)) {
            FocusOverlayView.setupFocusObserver(alertDialog);
        }
    }

    @NotificationConstants.Action
    public int getSelectedAction() {
        return selectedAction;
    }
}
