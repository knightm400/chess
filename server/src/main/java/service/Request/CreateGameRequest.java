package service.Request;

public record CreateGameRequest(String authToken, String gameName) {
}
