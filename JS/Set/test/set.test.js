var assert = require('assert') //Use var, const gave error
const SetCRDT = require('../set')

describe('SetCRDT', function () {
    it('should perform operations and maintain correct state', async function () {
        const set1 = new SetCRDT(1)
        const set2 = new SetCRDT(2)

        await set1.add(1)
        await set1.add(3)
        await set1.add(5)

        await set2.add(2)
        await set2.add(4)
        await set2.add(6)

        await set1.remove(5)
        await set2.remove(8)

        set1.print()
        set2.print()


        const b1 = set1.toMarshal()
        // console.log("b1: ", b1)
        const b2 = set2.toMarshal()

        const [rid1, updates1] = SetCRDT.fromMarshalData(b1)
        const [rid2, updates2] = SetCRDT.fromMarshalData(b2)

        const set3 = new SetCRDT(3)
        const set4 = new SetCRDT(4)

        await set3.merge(rid1, updates1)
        await set4.merge(rid2, updates2)

        set3.print()
        set4.print()

        assert.deepStrictEqual(set1.getValue(), set3.getValue(), "set1 and set3 are not equal")
        assert.deepStrictEqual(set2.getValue(), set4.getValue(), "set2 and set4 are not equal")
    })
})