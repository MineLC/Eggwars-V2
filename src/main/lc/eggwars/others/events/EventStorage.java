package lc.eggwars.others.events;

import java.util.SplittableRandom;

import lc.eggwars.EggwarsPlugin;
import lc.eggwars.game.GameInProgress;
import lc.eggwars.game.GameState;
import lc.eggwars.messages.Messages;
import lc.eggwars.others.events.special.DeathMatchEvent;
import lc.eggwars.others.events.special.PotionEvent;

public final class EventStorage {
    private static EventStorage storage;

    private static final SplittableRandom RANDOM = new SplittableRandom();

    private final EggwarsPlugin plugin;
    private final DeathMatchEvent.Data deathMatchData;
    private final PotionEvent.Data potionEvents;

    private final GameEvent[] baseEvents;
    private final int[] secondsToEvent;
    private final GameEvent deathMatchEvent;
    private final int startTimeTolerance;

    EventStorage(EggwarsPlugin plugin, DeathMatchEvent.Data deathmathData, GameEvent[] baseEvents, GameEvent deathMatchEvent, int eventTimeTolerance,  PotionEvent.Data potionEvents, int[] secondsToEvent) {
        this.plugin = plugin;
        this.baseEvents = baseEvents;
        this.deathMatchData = deathmathData;
        this.deathMatchEvent = deathMatchEvent;
        this.startTimeTolerance = eventTimeTolerance;
        this.potionEvents = potionEvents;
        this.secondsToEvent = secondsToEvent;
    }

    public GameEvent[] createEvents(final GameInProgress game) {
        final GameEvent[] events = new GameEvent[secondsToEvent.length + 1];
        events[secondsToEvent.length] = deathMatchEvent;

        for (int i = 0; i < secondsToEvent.length; i++) {
            final GameEvent baseEvent = baseEvents[RANDOM.nextInt(baseEvents.length)];
            if (game.getMapData().getMaxPersonsPerTeam() <= 1 && baseEvent.eventType() == GameEventType.TREASON) {
                --i;
                continue;
            }
            final int randomDiference = (secondsToEvent[i] * RANDOM.nextInt(startTimeTolerance)) / 100;

            final int startTime = RANDOM.nextBoolean()
                ? secondsToEvent[i] - randomDiference
                : secondsToEvent[i] + randomDiference;

            events[i] = new GameEvent(baseEvent.name(), baseEvent.information(), startTime, baseEvent.duration(), baseEvent.eventType());
        }

        return events;
    }

    public void loadEvent(final GameInProgress game, final GameEvent event) {
        if (game.getState() != GameState.IN_GAME) {
            return;
        }
        final EventCountdown countdown = new EventCountdown(game, event.name());
        countdown.setDuration(event.duration());

        switch (event.eventType()) {
            case DEATHMATCH:
                countdown.setTask(new DeathMatchEvent(game, deathMatchData, plugin));
                Messages.send(game.getPlayers(), "death-match.start-message");
                countdown.setDuration(Integer.MAX_VALUE);
                break;
            case TREASON:
                Messages.send(game.getPlayers(), "treason.start-message");
                break;
            case FATIGUE:
                Messages.send(game.getPlayers(), "fatigue.start-message");
                new PotionEvent(potionEvents.fatigueEvent(), game).execute(plugin);
                return;
            case WILLPOWER:
                Messages.send(game.getPlayers(), "willpower.start-message");
                new PotionEvent(potionEvents.willpower(), game).execute(plugin);
                return;
            case RUSH:
                Messages.send(game.getPlayers(), "rush.start-message");
                new PotionEvent(potionEvents.rush(), game).execute(plugin);
                return;
            case FULLHEALTH:
                Messages.send(game.getPlayers(), "fullheath.start-message");
                break;
            case CRITICAL:
                Messages.send(game.getPlayers(), "critical.start-message");
                break;
            case ONEDAMAGE:
                Messages.send(game.getPlayers(), "onedamage.start-message");
                break;
            case DISCOUNT:
                Messages.send(game.getPlayers(), "discount.start-message");
                break;
        }

        countdown.setId(plugin.getServer().getScheduler().runTaskTimer(plugin, countdown, 20, 0).getTaskId());
        game.setCountdown(countdown);
        game.setActiveEvent(event);
    }

    public static EventStorage getStorage() {
        return storage;
    }

    static void update(EventStorage newStorage) {
        storage = newStorage;
    }
}
