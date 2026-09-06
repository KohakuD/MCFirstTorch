package ch.minenox.firsttorch.client;

import com.mojang.blaze3d.platform.Window;
import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

public final class DevelopmentWindowController {
    private static final String LAYOUT_PROPERTY = "firsttorch.devWindowLayout";
    private static final String LEFT_TWO_THIRDS = "left_two_thirds";
    private static boolean applied;

    private DevelopmentWindowController() {
    }

    public static void applyIfRequested(Minecraft minecraft) {
        if (applied || !LEFT_TWO_THIRDS.equals(System.getProperty(LAYOUT_PROPERTY))) {
            return;
        }

        Window window = minecraft.getWindow();
        if (window.isFullscreen()) {
            window.toggleFullScreen();
            return;
        }

        long windowHandle = window.handle();
        long monitor = GLFW.glfwGetPrimaryMonitor();
        if (windowHandle == 0L || monitor == 0L) {
            return;
        }

        GLFW.glfwRestoreWindow(windowHandle);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer workX = stack.mallocInt(1);
            IntBuffer workY = stack.mallocInt(1);
            IntBuffer workWidth = stack.mallocInt(1);
            IntBuffer workHeight = stack.mallocInt(1);
            GLFW.glfwGetMonitorWorkarea(monitor, workX, workY, workWidth, workHeight);

            DevelopmentWindowLayout.WindowBounds target =
                    DevelopmentWindowLayout.leftTwoThirds(
                            new DevelopmentWindowLayout.WorkArea(
                                    workX.get(0),
                                    workY.get(0),
                                    workWidth.get(0),
                                    workHeight.get(0)));

            IntBuffer frameLeft = stack.mallocInt(1);
            IntBuffer frameTop = stack.mallocInt(1);
            IntBuffer frameRight = stack.mallocInt(1);
            IntBuffer frameBottom = stack.mallocInt(1);
            GLFW.glfwGetWindowFrameSize(
                    windowHandle,
                    frameLeft,
                    frameTop,
                    frameRight,
                    frameBottom);

            int contentWidth = Math.max(
                    320,
                    target.width() - frameLeft.get(0) - frameRight.get(0));
            int contentHeight = Math.max(
                    240,
                    target.height() - frameTop.get(0) - frameBottom.get(0));
            GLFW.glfwSetWindowSize(windowHandle, contentWidth, contentHeight);
            GLFW.glfwSetWindowPos(
                    windowHandle,
                    target.x() + frameLeft.get(0),
                    target.y() + frameTop.get(0));
            GLFW.glfwShowWindow(windowHandle);
            GLFW.glfwFocusWindow(windowHandle);
            applied = true;
        }
    }
}
