package model;

import java.util.Set;

public record GameData(Integer gameID, String whiteUsername, String blackUsername, String gameName, String gameData, String whiteColor, String blackColor) {}
