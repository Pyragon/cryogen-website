let renderer;
let scene;
let camera;

let cube;

let controls;

function loadScene(model) {
    console.log(model);
    scene = new THREE.Scene();

    let width = 250;
    let height = 400;

    camera = new THREE.PerspectiveCamera(75, width / height, 0.1, 6000);

    renderer = new THREE.WebGLRenderer();
    renderer.setSize(width, height);

    $('#model-viewer').append(renderer.domElement);

    //ADD CUBE
    let vertices = [];

    let hasAlpha = model.FaceAlphas != null;
    let hasFaceTypes = model.FaceType != null;

    //BUILD GEOMETRY
    for (let i = 0; i < model.FaceCount; i++) {

        let alpha = hasAlpha ? model.FaceAlphas[i] : 0;
        if (alpha == -1) continue;

        alpha = ~alpha & 0xFF;
        let faceType = hasFaceTypes ? model.FaceType[i] & 0x3 : 0;

        let faceA, faceB, faceC;
        switch (faceType) {
            case 0:
            case 1:
                faceA = model.TriangleX[i];
                faceB = model.TriangleY[i];
                faceC = model.TriangleZ[i];
                break;
            case 2:
            case 3:
                faceA = model.TexTriX[i];
                faceB = model.TexTriY[i];
                faceC = model.TexTriZ[i];
                break;
            default:
                throw new Error('Unknown face type=' + faceType);
        }

        let textureId = model.FaceTextures == null ? -1 : model.FaceTextures[i];

        let u, v;

        let colour = forHSBColour(model.FaceColor[i]);
        vertices.push({ pos: [model.VertexX[faceA], model.VertexY[faceA], model.VertexZ[faceA]], norm: [faceA, faceB, faceC] });
        vertices.push({ pos: [model.VertexX[faceB], model.VertexY[faceB], model.VertexZ[faceB]], norm: [faceA, faceB, faceC] });
        vertices.push({ pos: [model.VertexX[faceC], model.VertexY[faceC], model.VertexZ[faceC]], norm: [faceA, faceB, faceC] });
    }

    let positions = [];
    let normals = [];
    for (let vertex of vertices) {
        positions.push(...vertex.pos);
        normals.push(...vertex.norm);
    }

    let geometry = new THREE.BufferGeometry();
    geometry.setAttribute('position', new THREE.BufferAttribute(new Float32Array(positions), 3));
    geometry.setAttribute('normal', new THREE.BufferAttribute(new Float32Array(normals), 3));

    geometry.normalizeNormals();
    geometry.computeVertexNormals();


    let material = new THREE.MeshBasicMaterial({ color: 0x0000ff });
    cube = new THREE.Mesh(geometry, material);

    cube.rotation.x = Math.PI; //model renders upside down for some reason

    scene.add(cube);
    camera.position.z = 3000;

    controls = new THREE.TrackballControls(camera, renderer.domElement);
    controls.target.set(0, 500, 0);

    controls.addEventListener('end', function() {

    });

    controls.noPan = true;
    controls.rotateSpeed = 0.03;

    animate();
}

function animate() {
    requestAnimationFrame(animate);

    controls.update();

    renderer.render(scene, camera);
}