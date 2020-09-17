package Core.WorldView;

import static Core.WorldView.WorldViewStatus.*;

public class WorldViewController
{
    private static final String CLASSNAME = "WorldViewController ";
    private static WorldViewStatus worldViewStatus;

    public static void toggleInventory()
    {
        String methodName = "toggleInventory() ";
        if (worldViewStatus.equals(WORLD))
            setWorldViewStatus(INVENTORY);
        else if (worldViewStatus == INVENTORY_EXCHANGE || worldViewStatus == INVENTORY_SHOP || worldViewStatus == INVENTORY)
            setWorldViewStatus(WORLD);
        //System.out.println(CLASSNAME + methodName + worldViewStatus);
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
