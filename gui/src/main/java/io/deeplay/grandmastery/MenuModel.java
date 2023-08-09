package io.deeplay.grandmastery;

import io.deeplay.grandmastery.domain.GameMode;

public class MenuModel {
    private GameMode selectedGameMode;

    public GameMode getSelectedGameMode() {
        return selectedGameMode;
    }

    public void setSelectedGameMode(GameMode selectedGameMode) {
        this.selectedGameMode = selectedGameMode;
    }
}