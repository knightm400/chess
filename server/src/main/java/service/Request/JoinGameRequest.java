package service.Request;

public record JoinGameRequest(String authToken, Integer gameID, String playerColor) {}
