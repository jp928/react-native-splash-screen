/**
 * SplashScreen
 * 启动屏
 * from：http://www.devio.org
 * Author:CrazyCodeBoy
 * GitHub:https://github.com/crazycodeboy
 * Email:crazycodeboy@gmail.com
 */

#import "RNSplashScreen.h"
#import <React/RCTBridge.h>
#import <AVKit/AVkit.h>

static bool waiting = true;
static bool addedJsLoadErrorObserver = false;
static UIView* loadingView = nil;
static AVPlayerViewController* moviePlayerController = nil;
static AVPlayer* player;

@implementation RNSplashScreen
- (dispatch_queue_t)methodQueue{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE(SplashScreen)

+ (void)show {
    if (!addedJsLoadErrorObserver) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(jsLoadError:) name:RCTJavaScriptDidFailToLoadNotification object:nil];
        addedJsLoadErrorObserver = true;
    }

    while (waiting) {
        NSDate* later = [NSDate dateWithTimeIntervalSinceNow:0.1];
        [[NSRunLoop mainRunLoop] runUntilDate:later];
    }
}

+ (void)addVideoView: (UIView*)loadView {
    if (!moviePlayerController) {
        NSBundle *bundle = [NSBundle mainBundle];
        NSString *moviePath = [bundle pathForResource:@"splash" ofType:@"mp4"];
        NSURL *url = [NSURL fileURLWithPath:moviePath];
        
        player = [AVPlayer playerWithURL:url];
        
        moviePlayerController = [[AVPlayerViewController alloc] init];

        moviePlayerController.videoGravity = AVLayerVideoGravityResizeAspectFill;
        moviePlayerController.player = player;
        
        moviePlayerController.showsPlaybackControls = NO;
        
        [UIView transitionWithView:loadView duration:0.5
                options:UIViewAnimationOptionCurveEaseIn
                animations:^ { [loadView addSubview:moviePlayerController.view]; }
                completion:nil];
    }

}

+ (void)showSplash:(NSString*)splashScreen inRootView:(UIView*)rootView {
    if (!loadingView) {
        loadingView = [[[NSBundle mainBundle] loadNibNamed:splashScreen owner:self options:nil] objectAtIndex:0];
        CGRect frame = rootView.frame;
        frame.origin = CGPointMake(0, 0);
        loadingView.frame = frame;
        [self addVideoView: loadingView];
    }

    [rootView addSubview:loadingView];
}

+ (void)hide {
    [player play];
    CMTime timeInterval = CMTimeMakeWithSeconds(0.2, NSEC_PER_SEC);
    dispatch_queue_t mainQueue = dispatch_get_main_queue();
    [player addPeriodicTimeObserverForInterval:(timeInterval) queue:mainQueue usingBlock:^(CMTime time){
      NSTimeInterval seconds = CMTimeGetSeconds(time);
      NSInteger intSec = seconds;
         
      if (intSec > 1.8) {
        waiting = false;
        
        [UIView animateWithDuration:0.5
            animations:^{loadingView.alpha = 0.0;}
            completion:^(BOOL finished){ [loadingView removeFromSuperview]; }];
      }
     }];
}

+ (void) jsLoadError:(NSNotification*)notification
{
    // If there was an error loading javascript, hide the splash screen so it can be shown.  Otherwise the splash screen will remain forever, which is a hassle to debug.
    [RNSplashScreen hide];
}

RCT_EXPORT_METHOD(hide) {
    [RNSplashScreen hide];
}

RCT_EXPORT_METHOD(show) {
    [RNSplashScreen show];
}

@end
