var  assert  = require('assert') //Use var, const gave error
const Counter = require('../counter')

describe('Counter', function () {
    it('should perform operations and maintain correct state', async function () {
        const counter1 = new Counter(1)
        const counter2 = new Counter(2)

        await counter1.inc()
        await counter1.inc()

        await counter2.inc()
        await counter2.inc()
        await counter2.inc()

        await counter1.dec()
        await counter2.dec()

        console.log(counter1.print()) // Expected: Counter_1:1
        console.log(counter2.print()) // Expected: Counter_2:2

        assert.strictEqual(counter1.getValue(), 1)
        assert.strictEqual(counter2.getValue(), 2)

        const b1 = counter1.toMarshal()
        // console.log("b1: ", b1)
        const b2 = counter2.toMarshal()

        const [rid1, updates1] = Counter.fromMarshalData(b1)
        const [rid2, updates2] = Counter.fromMarshalData(b2)

        const counter3 = new Counter(3)
        const counter4 = new Counter(4)

        await counter3.merge(rid1, updates1)
        await counter4.merge(rid2, updates2)

        console.log(counter3.print()) // Expected: Counter_3:1
        console.log(counter4.print()) // Expected: Counter_4:2

        assert.strictEqual(counter1.getValue(), counter3.getValue())
        assert.strictEqual(counter2.getValue(), counter4.getValue())
    })
})