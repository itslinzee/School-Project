package game;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Random;

import java.util.stream.IntStream;
import java.util.function.IntFunction;
import java.util.function.Function;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.counting;
import static game.MasterMind.Response.*;

public class MasterMind {

	public enum Response {NO_MATCH, MATCH, POSITIONAL_MATCH}

	public enum GameStatus {IN_PROGRESS, WON, LOST}

	protected int tries = 0;
	private static final int SIZE = 6;

	private List<Color> selection;
	private GameStatus gameStatus = GameStatus.IN_PROGRESS;

	public MasterMind()
	{
		selection = generateRandomColors(System.currentTimeMillis());
	}

	protected MasterMind(List<Color> generatedCode) {
		selection = generatedCode;
	}

	public Map<Response, Long> guess(List<Color> guess) {
		tries++;

		IntFunction<Response> computeMatchAtPosition = index ->
				selection.get(index) == guess.get(index) ? POSITIONAL_MATCH :
						guess.contains(selection.get(index)) ? MATCH : NO_MATCH;

		Map<Response, Long> response =
				IntStream.range(0, SIZE)
						.mapToObj(computeMatchAtPosition)
						.collect(groupingBy(Function.identity(), counting()));

		response.computeIfAbsent(NO_MATCH, key -> 0L);
		response.computeIfAbsent(MATCH, key -> 0L);
		response.computeIfAbsent(POSITIONAL_MATCH, key -> 0L);

		updateGameStatus(response);

		return response;
	}

	public GameStatus getGameStatus() {
		return gameStatus;
	}

	private void updateGameStatus(Map<Response, Long> response) {
		if (response.get(Response.POSITIONAL_MATCH) == 6 && tries <= 20)
			gameStatus = GameStatus.WON;
		else {
			if (tries > 20)
				gameStatus = GameStatus.LOST;
		}
	}

	protected List<Color> generateRandomColors(long seed) {
		Random random = new Random(seed);

		shuffle(getAvailableColors(), random);

		List<Color> tempColor = Arrays.asList(
				getAvailableColors().get(random.nextInt(10)),
				getAvailableColors().get(random.nextInt(10)),
				getAvailableColors().get(random.nextInt(10)),
				getAvailableColors().get(random.nextInt(10)),
				getAvailableColors().get(random.nextInt(10)),
				getAvailableColors().get(random.nextInt(10)));

		for(int i = 0; i < 6; i++)
		{
			System.out.println("Index: " + i + " Color: " + tempColor.get(i));
		}

		return tempColor;

	}

	public List<Color> getAvailableColors()
	{
		return Arrays.asList(Color.CYAN, Color.ORANGE, Color.RED, Color.BLUE, Color.GREEN,
				Color.MAGENTA, Color.YELLOW, Color.PINK, Color.WHITE, Color.DARK_GRAY);
	}
}





