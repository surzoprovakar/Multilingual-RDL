var assert = require('assert') //Use var, const gave error
const MapCRDT = require('../map')

describe('MapCRDT', function () {
    it('should perform operations and maintain correct state', async function () {
        const map1 = new MapCRDT(1)
        const map2 = new MapCRDT(2)

        await map1.add("a", 1)
        await map1.add("b", 3)
        await map1.add("c", 5)

        await map2.add("p", 2)
        await map2.add("q", 4)
        await map2.add("r", 6)

        await map1.delete("d")
        await map2.delete("r")

        await map1.update("c", 7)
        await map2.update("r", 8)

        map1.print()
        map2.print()


        const b1 = map1.toMarshal()
        // console.log("b1: ", b1)
        const b2 = map2.toMarshal()

        const [rid1, updates1] = MapCRDT.fromMarshalData(b1)
        const [rid2, updates2] = MapCRDT.fromMarshalData(b2)

        const map3 = new MapCRDT(3)
        const map4 = new MapCRDT(4)

        await map3.merge(rid1, updates1)
        await map4.merge(rid2, updates2)

        map3.print()
        map4.print()

        assert.deepStrictEqual(map1.getValue(), map3.getValue(), "map1 and map3 are not equal")
        assert.deepStrictEqual(map2.getValue(), map4.getValue(), "map2 and map4 are not equal")
    })
})