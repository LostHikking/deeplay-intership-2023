package io.deeplay.grandmastery;

import io.deeplay.grandmastery.domain.GameMode;
import javafx.application.Platform;
import javafx.fxml.FXML;

    public class MenuController {
        private MenuModel menuModel;

        public void setMenuModel(MenuModel menuModel) {
            this.menuModel = menuModel;
        }

        @FXML
        private void handleStartHumanVsBot() {
            menuModel.setSelectedGameMode(GameMode.HUMAN_VS_BOT);
            //здесь нужно начать процесс в gamecontroller
            //а также закрыть само меню, аналогично с некст методом
        }

        @FXML
        private void handleStartBotVsBot() {
            menuModel.setSelectedGameMode(GameMode.BOT_VS_BOT);
        }

        @FXML
        private void handleExit() {
            Platform.exit();
        }
    }
