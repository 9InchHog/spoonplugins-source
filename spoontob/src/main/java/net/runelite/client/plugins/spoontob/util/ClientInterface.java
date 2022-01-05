package net.runelite.client.plugins.spoontob.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Projectile;
import net.runelite.api.Renderable;
import net.runelite.api.SpritePixels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientInterface {
    private static final Logger log = LoggerFactory.getLogger(ClientInterface.class);
    private static Method getInteracting = null;
    private static Method setLoginScreenBackgroundPixels = null;
    private static Method rightSpriteOverwrite = null;
    private static Method leftSpriteOverwrite = null;
    private static HashSet<String> getNpcsToHide = null;
    private static HashSet<String> getNpcsToHideOnDeath = null;
    private static HashSet<Integer> getNpcsByAnimationToHideOnDeath = null;
    private static HashSet<Integer> getNpcsByIdToHideOnDeath = null;
    private static Method setDeadNPCsHidden = null;

    public static void setHidden(Renderable renderable, boolean hidden) {
        Method setHidden = null;

        try {
            setHidden = renderable.getClass().getMethod("setHidden", Boolean.TYPE);
        } catch (NoSuchMethodException var5) {
            log.debug("Couldn't find method setHidden for class {}", renderable.getClass());
            return;
        }

        try {
            setHidden.invoke(renderable, hidden);
        } catch (InvocationTargetException | IllegalAccessException var4) {
            log.debug("Couldn't call method setHidden for class {}", renderable.getClass());
        }

    }

    public static boolean getHidden(Renderable renderable) {
        Method getHidden = null;

        try {
            getHidden = renderable.getClass().getMethod("getHidden");
        } catch (NoSuchMethodException var4) {
            log.debug("Couldn't find method getHidden for class {}", renderable.getClass());
            return false;
        }

        try {
            return (Boolean)getHidden.invoke(renderable);
        } catch (InvocationTargetException | IllegalAccessException var3) {
            log.debug("Couldn't call method getHidden for class {}", renderable.getClass());
            return false;
        }
    }

    public static Actor getInteracting(Projectile p) {
        if (getInteracting == null) {
            try {
                getInteracting = p.getClass().getMethod("getInteracting");
            } catch (NoSuchMethodException var3) {
                log.debug("Couldn't find method getInteracting for class {}", p.getClass());
                return null;
            }
        }

        try {
            return (Actor)getInteracting.invoke(p);
        } catch (InvocationTargetException | IllegalAccessException var2) {
            log.debug("Couldn't call method getInteracting for class {}", p.getClass());
            return null;
        }
    }

    public static void setLoginScreenBackgroundPixels(Client client, SpritePixels spritePixels) {
        if (setLoginScreenBackgroundPixels == null) {
            try {
                setLoginScreenBackgroundPixels = client.getClass().getMethod("setLoginScreenBackgroundPixels", SpritePixels.class);
            } catch (NoSuchMethodException var4) {
                log.debug("Couldn't find method setLoginScreenBackgroundPixels for class {}", client.getClass());
                return;
            }
        }

        try {
            setLoginScreenBackgroundPixels.invoke(client, spritePixels);
        } catch (InvocationTargetException | IllegalAccessException var3) {
            log.debug("Couldn't call method setLoginScreenBackgroundPixels for class {}", client.getClass());
        }

    }

    public static void leftSpriteOverwrite(Client client) {
        if (leftSpriteOverwrite == null) {
            try {
                leftSpriteOverwrite = client.getClass().getMethod("leftSpriteOverwrite");
            } catch (NoSuchMethodException var3) {
                log.debug("Couldn't find method leftSpriteOverwrite for class {}", client.getClass());
                return;
            }
        }

        try {
            leftSpriteOverwrite.invoke(client);
        } catch (InvocationTargetException | IllegalAccessException var2) {
            log.debug("Couldn't call method leftSpriteOverwrite for class {}", client.getClass());
        }

    }

    public static void rightSpriteOverwrite(Client client) {
        if (rightSpriteOverwrite == null) {
            try {
                rightSpriteOverwrite = client.getClass().getMethod("rightSpriteOverwrite");
            } catch (NoSuchMethodException var3) {
                log.debug("Couldn't find method rightSpriteOverwrite for class {}", client.getClass());
                return;
            }
        }

        try {
            rightSpriteOverwrite.invoke(client);
        } catch (InvocationTargetException | IllegalAccessException var2) {
            log.debug("Couldn't call method rightSpriteOverwrite for class {}", client.getClass());
        }

    }

    public static void setDeadNPCsHidden(Client client, boolean hidden) {
        if (setDeadNPCsHidden == null) {
            try {
                setDeadNPCsHidden = client.getClass().getMethod("setDeadNPCsHidden", Boolean.TYPE);
            } catch (NoSuchMethodException var4) {
                log.debug("Couldn't find method setDeadNPCsHidden for class {}", client.getClass());
                return;
            }
        }

        try {
            setDeadNPCsHidden.invoke(client, hidden);
        } catch (InvocationTargetException | IllegalAccessException var3) {
            log.debug("Couldn't call method setDeadNPCsHidden for class {}", client.getClass());
        }

    }

    public static HashSet<String> getNpcsToHide(Client client) {
        if (getNpcsToHide == null) {
            try {
                Method m = client.getClass().getMethod("getNpcsToHide");

                try {
                    getNpcsToHide = (HashSet)m.invoke(client);
                } catch (InvocationTargetException | IllegalAccessException var3) {
                    log.debug("Couldn't call method getNpcsToHide for class {}", client.getClass());
                    return null;
                }
            } catch (NoSuchMethodException var4) {
                log.debug("Couldn't find method getNpcsToHide for class {}", client.getClass());
                return null;
            }
        }

        return getNpcsToHide;
    }

    public static HashSet<String> getNpcsToHideOnDeath(Client client) {
        if (getNpcsToHideOnDeath == null) {
            try {
                Method m = client.getClass().getMethod("getNpcsToHideOnDeath");

                try {
                    getNpcsToHideOnDeath = (HashSet)m.invoke(client);
                } catch (InvocationTargetException | IllegalAccessException var3) {
                    log.debug("Couldn't call method getNpcsToHideOnDeath for class {}", client.getClass());
                    return null;
                }
            } catch (NoSuchMethodException var4) {
                log.debug("Couldn't find method getNpcsToHideOnDeath for class {}", client.getClass());
                return null;
            }
        }

        return getNpcsToHideOnDeath;
    }

    public static HashSet<Integer> getNpcsByAnimationToHideOnDeath(Client client) {
        if (getNpcsByAnimationToHideOnDeath == null) {
            try {
                Method m = client.getClass().getMethod("getNpcsByAnimationToHideOnDeath");

                try {
                    getNpcsByAnimationToHideOnDeath = (HashSet)m.invoke(client);
                } catch (InvocationTargetException | IllegalAccessException var3) {
                    log.debug("Couldn't call method getNpcsByAnimationToHideOnDeath for class {}", client.getClass());
                    return null;
                }
            } catch (NoSuchMethodException var4) {
                log.debug("Couldn't find method getNpcsByAnimationToHideOnDeath for class {}", client.getClass());
                return null;
            }
        }

        return getNpcsByAnimationToHideOnDeath;
    }

    public static HashSet<Integer> getNpcsByIdToHideOnDeath(Client client) {
        if (getNpcsByIdToHideOnDeath == null) {
            try {
                Method m = client.getClass().getMethod("getNpcsByIdToHideOnDeath");

                try {
                    getNpcsByIdToHideOnDeath = (HashSet)m.invoke(client);
                } catch (InvocationTargetException | IllegalAccessException var3) {
                    log.debug("Couldn't call method getNpcsByIdToHideOnDeath for class {}", client.getClass());
                    return null;
                }
            } catch (NoSuchMethodException var4) {
                log.debug("Couldn't find method getNpcsByIdToHideOnDeath for class {}", client.getClass());
                return null;
            }
        }

        return getNpcsByIdToHideOnDeath;
    }
}
