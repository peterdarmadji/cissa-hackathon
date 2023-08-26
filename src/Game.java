import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.Random;


public class Game extends AbstractGame {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int MAX_NUM_PARTICLE = 50;
    private static final double X_PADDING = 25;
    private static final double Y_PADDING = 50;
    private Particle[] particles = new Particle[MAX_NUM_PARTICLE];
    private int numParticle = 0;
    private Particle p1;
    private Particle p2;
    private Font textOutput; // Outputting texts
    private Player player1;
    private Player player2;
    private DrawOptions drawOptions; // Scale the other visuals than players
    private Image backgroundImage;
    private Image homeImage;
    private Font titleText;
    private Font timerText;
    private Font subtitleText;
    private boolean hasStarted = false;
    private int timer = 120;


    /**
     * Randomizing particles' placement
     */
    private Particle randomParticle() {
        Random rand = new Random();
        boolean isIntersectingOtherParticle = true;
        Particle newParticle = null;

        // checks whether the particle intersects with other
        // particle that has been created
        do {
            // first create a new particle with a random coordinate,
            // and particleType
            double x = rand.nextDouble() * (WIDTH - (2  * X_PADDING)) + X_PADDING;
            double y = rand.nextDouble() * (HEIGHT - (2 * Y_PADDING)) + Y_PADDING;
            Point particlePoint = new Point(x, y);

            int particleType = Particle.generateRandomParticleType();

            newParticle = new Particle(particleType, particlePoint);

            // check whether it intersects with other particle
            isIntersectingOtherParticle = false;
            for (int j = 0; j < numParticle; j++) {
                // check if it intersects with other particle, if yes,
                // then continue creating a new particle, until we found one
                // that doesn't intersect with other particle
                if (particles[j].isIntersect(newParticle)) {
                    isIntersectingOtherParticle = true;
                    break;
                }
            }
        } while (isIntersectingOtherParticle);

        return newParticle;
    }

    /**
     * Constructors for the game
     */
    public Game() {
        super(WIDTH, HEIGHT, "Game Title");
        p1 = new Particle(1, new Point(100, 200));
        p2 = new Particle(2, new Point(200, 200));

        textOutput = new Font("res/conformable.otf", 90);
        timerText = new Font("res/conformable.otf", 120);
        backgroundImage = new Image("res/backgroundblack.jpeg");
        homeImage = new Image("res/homescreen.jpeg");
        titleText = new Font("res/arcadeclassic.ttf", 70); // Adjust the font path and size
        subtitleText = new Font("res/conformable.otf", 40);
        drawOptions = new DrawOptions();
        player1 = new Player(new Point(200, 350), new Image("res/playerBlue.png"));
        player2 = new Player(new Point(1900, 50), new Image("res/playerRed.png"));

        for (int i = 0; i < MAX_NUM_PARTICLE; i++) {
            particles[numParticle] = randomParticle();
            numParticle++;
        }
    }

    /**
     * Calculate the Euclidean distance between 2 points
     */
    public int distance(Point point1, Point point2) {
        return (int) Math.abs(
                Math.sqrt(Math.pow((point1.x - point2.x), 2)
                        + Math.pow((point1.y - point2.y), 2)));
    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        Game game = new Game();
        game.run();
    }

    /**
     * Performs a state update. This simple example shows an image that can be controlled with the arrow keys, and
     * allows the game to exit when the escape key is pressed.
     */
    @Override
    public void update(Input input) {
        if (hasStarted) {
            // Placement of BG image
            backgroundImage.draw(0,0, drawOptions.setScale(2,2));

            // Players' movement
            player1.movement1(player1, input);
            player2.movement2(player2, input);

            // Checking, hiding and drawing particles
            for (Particle particle : particles) {
                if (particle.getHidden()) continue;


                if (player1.isIntersect(particle, 70)) {
                    player1.particleIntersectBehaviour(particle);
                } else if (player2.isIntersect(particle, 70)) {
                    player2.particleIntersectBehaviour(particle);
                }
                particle.draw();
            }

            // Checking for player intersecting player
            if (player1.isIntersect(player2, 60) && player1.getScaleSize() > player2.getScaleSize()) {
                player1.playerIntersectBehaviour(player2);
                player1.respawn();
                player2.respawn();
            }
            else if (player2.isIntersect(player1, 60) && player2.getScaleSize() > player1.getScaleSize()) {
                player2.playerIntersectBehaviour(player1);
                player1.respawn();
                player2.respawn();
            } else {
                player1.draw();
                player2.draw();
            }

            timer--;
            /* Output all the visuals */
            timerText.drawString(String.valueOf(timer), 950, 100);
            textOutput.drawString("Score 1 : " + player1.score, 45, 100);
            textOutput.drawString("Score 2 : " + player2.score, 45, 200);

        } else {
            double titleX = Window.getWidth() / 2.0 - titleText.getWidth("GAME TITLE") / 2;
            double titleY = Window.getHeight() / 2.0 + 70.0 / 2.0;

            double subtitleX = Window.getWidth() / 2.0 - subtitleText.getWidth("PRESS ENTER TO PLAY") / 2;
            double subtitleY = Window.getHeight() / 2.0 + 40.0 / 2.0 + 55;

            homeImage.draw(Window.getWidth() / 2.0,Window.getHeight() / 2.0, drawOptions.setScale(1.5, 1.5));
            titleText.drawString("GAME TITLE", titleX, titleY);
            subtitleText.drawString("PRESS ENTER TO PLAY", subtitleX, subtitleY);
            if (input.wasPressed(Keys.ENTER)) {
                hasStarted = true;
            }
        }
    }
}
