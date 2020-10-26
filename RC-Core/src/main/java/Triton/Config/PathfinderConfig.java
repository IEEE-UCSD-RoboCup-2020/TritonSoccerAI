package Triton.Config;

public class PathfinderConfig {
    // Theta*
    public static final int NODE_RADIUS = 40;
    public static final int NODE_DIAMETER = NODE_RADIUS * 2;
    public static final double ADD_DIST = 20;
    public static final double SAFE_DIST = PathfinderConfig.NODE_RADIUS + ObjectConfig.ROBOT_RADIUS
                                + PathfinderConfig.ADD_DIST;
}