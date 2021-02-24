let HSL_2_RGB = Array(65535);

let var1 = 0.7 + (Math.random() * 0.03 - 0.015);
let var3 = 0;

for (let var4 = 0; var4 < 512; ++var4) {
    let var5 = (0.0078125 + (var4 >> 3) / 64.0) * 360.0;
    let var6 = 0.0625 + (var4 & 7) / 8.0;

    for (let var7 = 0; var7 < 128; ++var7) {
        let var8 = var7 / 128.0;
        let var9 = 0.0;
        let var10 = 0.0;
        let var11 = 0.0;
        let var12 = var5 / 60.0;
        let var13 = var12;
        let var14 = var13 % 6;
        let var15 = var12 - var13;
        let var16 = (1.0 - var6) * var8;
        let var17 = var8 * (1.0 - var6 * var15);
        let var18 = var8 * (1.0 - var6 * (1.0 - var15));
        if (var14 == 0) {
            var9 = var8;
            var10 = var18;
            var11 = var16;
        } else if (var14 == 1) {
            var9 = var17;
            var10 = var8;
            var11 = var16;
        } else if (2 == var14) {
            var9 = var16;
            var10 = var8;
            var11 = var18;
        } else if (var14 == 3) {
            var9 = var16;
            var10 = var17;
            var11 = var8;
        } else if (4 == var14) {
            var9 = var18;
            var10 = var16;
            var11 = var8;
        } else if (5 == var14) {
            var9 = var8;
            var10 = var16;
            var11 = var17;
        }

        var9 = Math.pow(var9, var1);
        var10 = Math.pow(var10, var1);
        var11 = Math.pow(var11, var1);
        let var19 = (var9 * 256.0);
        let var20 = (256.0 * var10);
        let var21 = (256.0 * var11);
        let var22 = (var19 << 16) + -16777216 + (var20 << 8) + var21;
        HSL_2_RGB[var3++] = var22;
    }
}

function forHSBColour(hsb) {
    return HSL_2_RGB[hsb & 0xFFFF] & 16777215;
}