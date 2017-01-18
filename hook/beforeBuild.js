module.exports = function(ctx) {
    // make sure android platform is part of build
    if (ctx.opts.platforms.indexOf('android') < 0) {
        return;
    }
    var fs = ctx.requireCordovaModule('fs'),
        path = ctx.requireCordovaModule('path'),
        deferral = ctx.requireCordovaModule('q').defer();

    var platformRoot = path.join(ctx.opts.projectRoot, 'platforms/android/build-extras.gradle');
    var buildextraspos = path.join(ctx.opts.plugin.dir, 'build-extras.gradle');

    var reader = fs.createReadStream(buildextraspos);
    var writer = fs.createWriteStream(platformRoot);

    writer.on('pipe', function(src){
         console.log('copying ' + buildextraspos + 'to ' + platformRoot);
        deferral.resolve();
    })
    reader.pipe(writer);

    return deferral.promise;
};