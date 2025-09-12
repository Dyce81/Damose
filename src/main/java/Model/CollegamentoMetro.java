package Model;

/**
 * Questo record è necessario per capire quali linee della metropolitana passano per una fermata della metro.
 * In stop_times.txt mancano gli orari delle fermate della metropolitana, quindi non è possibile mettere
 * in relazione trips con routes e routes con stops. Questa classe mantiene le informazioni di un file
 * creato da noi per vedere a quale fermata corrispondono quali linee della metro.
 */

public record CollegamentoMetro(String stopId, String routeId) {}