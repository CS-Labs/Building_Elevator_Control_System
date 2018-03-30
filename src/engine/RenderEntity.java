package engine;

import javafx.scene.paint.Color;

/**
 * Implement this class for each object you want to be able to add to
 * the world, render it and have it move around.
 *
 * @author Justin Hall
 */
public abstract class RenderEntity extends ActorGraph implements PulseEntity {
    private String _texture;
    private Color _color = Color.RED;

    /**
     * This function ensures that the render entity is added to the world. After
     * calling this it will be regularly called by the Engine and its movement
     * will be calculated by the Renderer and it will be drawn on the screen.
     */
    public void addToWorld()
    {
        Engine.getMessagePump().sendMessage(new Message(Singleton.ADD_PULSE_ENTITY, this));
        Engine.getMessagePump().sendMessage(new Message(Singleton.ADD_RENDER_ENTITY, this));
    }

    /**
     * After calling this the entity will no longer be drawn and its update function will
     * not be called
     */
    public void removeFromWorld()
    {
        Engine.getMessagePump().sendMessage(new Message(Singleton.REMOVE_PULSE_ENTITY, this));
        Engine.getMessagePump().sendMessage(new Message(Singleton.REMOVE_RENDER_ENTITY, this));
    }

    public void setTexture(String texture)
    {
        _texture = texture;
        Engine.getMessagePump().sendMessage(new Message(Singleton.REGISTER_TEXTURE, texture));
    }

    public void setColor(Color color)
    {
        _color = color;
    }

    public String getTexture()
    {
        return _texture;
    }

    public Color getColor()
    {
        return _color;
    }
}
