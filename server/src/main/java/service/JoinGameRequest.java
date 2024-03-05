package service;

public record JoinGameRequest(String authToken, String gameID, String playerColor) {}
