package Triton.Config;

/**
 * Config file for pathfinding
 */
public class PathfinderConfig {
    // Theta* / JPS
    public static final int NODE_RADIUS = 30;
    public static final int NODE_DIAMETER = NODE_RADIUS * 2;
    public static final double ADD_DIST = 0;
    public static final double SAFE_DIST = PathfinderConfig.NODE_RADIUS + ObjectConfig.ROBOT_RADIUS
            + PathfinderConfig.ADD_DIST;
    public static final double BOUNDARY_EXTENSION = 800;

    public static final double SPRINT_TO_ROTATE_DIST_THRESH = 500; // Face closest node outside of this threshold
    public static final double MOVE_ANGLE_THRESH = 20; // Don't move if outside angle threshold
    public static final double RD_SWITCH_ROTATE_ANGLE_THRESH = 40; // Outside angle threshold, use RV, inside angle, use RD
    public static final double BALL_CAP_DIST_THRESH = 500; // Turn on autocap if ball is within threshold
}