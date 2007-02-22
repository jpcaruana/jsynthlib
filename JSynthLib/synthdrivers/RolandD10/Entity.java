/*
 * Copyright 2006 Roger Westerlund
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package synthdrivers.RolandD10;

/**
 * This class holds an integer value which can be retrieved either as
 * an integer value or as a data value in the way the Roland D10
 * represents large numbers in its transfer protocol. The data value is
 * a representation where only the seven least significant bits of a byte
 * holds information.
 * 
 * The class also provides some arithmetic operations to be performed on
 * the objects of this class.
 * 
 * @author Roger Westerlund
 */
public class Entity {

    public static final Entity ZERO = Entity.createFromIntValue(0);

    private static final int BIT_MASK_7 = 0x7f;

    private static final int NOT_SET = -1;

    private int intValue = NOT_SET;

    private int dataValue = NOT_SET;

    /**
     * Constructor not visible. No external instance creations.
     */
    private Entity() {
    }

    /**
     * Creates an entity object from the supplied int value.
     * 
     * @param intValue
     *      The value the created object should hold.
     * @return
     *      An entity object for this int value.
     */
    public static Entity createFromIntValue(int intValue) {
        Entity entity = new Entity();
        entity.intValue = intValue;
        return entity;
    }

    /**
     * Creates an entity object from the supplied data value.
     * 
     * @param dataValue
     *      The value the created object should hold.
     * @return
     *      An entity object for this data value.
     */
    public static Entity createFromDataValue(int dataValue) {
        Entity entity = new Entity();
        entity.dataValue = dataValue;
        return entity;
    }

    /**
     * Creates a new entity object which is the sum of this object and the
     * supplied object.
     * 
     * @param entity
     *      The entity to add to this entity.
     * @return
     *      The entity that is the sum of the two entities.
     */
    public Entity add(Entity entity) {
        return Entity.createFromIntValue(getIntValue() + entity.getIntValue());
    }

    /**
     * Creates a new entity object which is the difference of this object and the
     * supplied object.
     * 
     * @param entity
     *      The entity to subtract from this entity.
     * @return
     *      The entity that is the difference of the two entities.
     */
    public Entity subtract(Entity entity) {
        return Entity.createFromIntValue(getIntValue() - entity.getIntValue());
    }

    /**
     * Creates a new entity object which is the product of this object and the
     * supplied object.
     * 
     * @param entity
     *      The entity to multiply with this entity.
     * @return
     *      The entity that is the product of the two entities.
     */
    public Entity multiply(Entity entity) {
        return Entity.createFromIntValue(getIntValue() * entity.getIntValue());
    }

    /**
     * @return
     *		Returns the dataValue.
     */
    public int getDataValue() {
        if (NOT_SET == dataValue) {
            dataValue = to7Bits(intValue);
        }
        return dataValue;
    }

    /**
     * @return
     *		Returns the intValue.
     */
    public int getIntValue() {
        if (NOT_SET == intValue) {
            intValue = from7Bits(dataValue);
        }
        return intValue;
    }

    /**
     * Converts a Roland 7 bit hex entity to a number.
     * 
     * @param entity The 7 bit hex entity to convert.
     * @return The number corresponding to the parameter.
     */
    private int from7Bits(int entity) {
        int result = 0;
        int bitMask = BIT_MASK_7;
        for (int position = 0; position < 4; position++) {
            result |= entity & bitMask;
            bitMask <<= 7;
            entity >>= 1;
        }
        return result;
    }

    /**
     * Converts a number to a Roland 7 bit hex entity.
     * 
     * @param number The number to convert.
     * @return The 7 bit hex entity corresponding to the parameter.
     */
    private int to7Bits(int number) {
        int result = 0;
        int bitMask = BIT_MASK_7;
        for (int position = 0; position < 4; position++) {
            result |= number & bitMask;
            bitMask <<= 8;
            number <<= 1;
        }
        return result;
    }

}
