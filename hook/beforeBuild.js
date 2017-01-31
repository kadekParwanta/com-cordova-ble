module.exports = function (ctx) {
    // make sure android platform is part of build
    if (ctx.opts.platforms.indexOf('android') < 0) {
        return;
    }
    var fs = ctx.requireCordovaModule('fs'),
        path = ctx.requireCordovaModule('path'),
        deferral = ctx.requireCordovaModule('q').defer();

    var platformRoot = path.join(ctx.opts.projectRoot, 'platforms/android');
    var gradleFileLocation = path.join(platformRoot, 'build.gradle');
    // var buildextraspos = path.join(ctx.opts.plugin.dir, 'build-extras.gradle');

    // var reader = fs.createReadStream(buildextraspos);
    // var writer = fs.createWriteStream(platformRoot);

    // writer.on('pipe', function(src){
    //     console.log('copying ' + buildextraspos + 'to ' + platformRoot);
    //     deferral.resolve();
    // })
    // reader.pipe(writer);

    fs.readFile(gradleFileLocation, 'utf-8', function (err, data) {
        if (err) throw err;

        var newValue = data.replace('classpath', 'classpath \'io.realm:realm-gradle-plugin:2.1.0\'\nclasspath');
        newValue = newValue.replace('jcenter()','mavenCentral()\njcenter()')

        fs.writeFile(gradleFileLocation, newValue, 'utf-8', function (err) {
            if (err) throw err;
            console.log('build.gradle complete');
            deferral.resolve();
        });
    });

    return deferral.promise;
};