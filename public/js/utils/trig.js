var Trig = Trig || {};

Trig.Trig = function Trig() {

    let SINE = Array(16384);
    let COSINE = Array(16384);

    this.init = function() {
        let step = 3.834951969714103E-4;

        for (let i = 0; i < 16384; i++) {
            SINE[i] = 16384.0 * Math.sin(i * step);
            COSINE[i] = 16384.0 * Math.cos(i * step);
        }
    }

    this.getSine = function(i) {
        return SINE[i];
    }

    this.getCosine = function(i) {
        return COSINE[i];
    }

    this.init();

}