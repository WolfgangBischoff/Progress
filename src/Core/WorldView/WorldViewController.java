package Core.WorldView;

import static Core.WorldView.WorldViewStatus.*;

public class WorldViewController
{
    private static final String CLASSNAME = "WorldViewController ";
    private static WorldViewStatus worldViewStatus;

    public static void command(String cmd)
    {
        if (cmd.equals("TAB"))
        {
            if (worldViewStatus.equals(WORLD))
                setWorldViewStatus(INVENTORY);
            else if (worldViewStatus.equals(INVENTORY))
                setWorldViewStatus(WORLD);
            else if (worldViewStatus == INVENTORY_EXCHANGE || worldViewStatus == INVENTORY_SHOP)
                setWorldViewStatus(WORLD);
        }
    }

    public static WorldViewStatus getWorldViewStatus()
    {
        return worldViewStatus;
    }

    public static void setWorldViewStatus(WorldViewStatus worldViewStatus)
    {
        String methodName = "setWorldStatus() ";
        boolean debug = false;

        if (debug)
            System.out.println(CLASSNAME + methodName + "New status: " + worldViewStatus);
        WorldViewController.worldViewStatus = worldViewStatus;
    }
}
