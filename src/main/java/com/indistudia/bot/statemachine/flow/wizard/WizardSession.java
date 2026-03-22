package com.indistudia.bot.statemachine.flow.wizard;

import com.indistudia.domain.Media;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WizardSession {
    private WizardStep step = WizardStep.WAITING_TITLE;
    private List<Media> candidates;
    private String searchQuery;
    private Media selectedMedia;
}
