//
//  FirstViewController.m
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <QuartzCore/CoreAnimation.h>

#import "StreamingViewController.h"
#import "AppDelegate.h"

#import "../libs/audiostreamer/AudioStreamer.h"
#import "../utils/DeviceUtils.h"
#import "../delegates/StreamingPlayerDelegate.h"

@interface StreamingViewController ()

@end

@implementation StreamingViewController

@synthesize titleLabel;
@synthesize singerLabel;
@synthesize playPauseButton;
@synthesize coverImageView;

@synthesize currentTitle;
@synthesize currentArtist;

#pragma mark - Controller lifecycle

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
	NSLog(@"StreamingViewController - initWithNibName");
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
	
    if (self) {
        self.title = NSLocalizedString(@"CONTROLLER_TITLE_ONAIR", @"");
        self.tabBarItem.image = [UIImage imageNamed:@"onair"];
    }
    NSLog(@"init streaming view controller");
    return self;
}
							
- (void)viewDidLoad
{
    [super viewDidLoad];
	NSLog(@"StreamingViewController - viewDidLoad");

	if(streamer == nil || ![streamer isPlaying] || ![streamer isWaiting]) {
		[self clearUi: YES];
	}

	[self updateUi];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
}

- (void) viewWillAppear:(BOOL)animated
{
	NSLog(@"StreamingViewController - viewWillAppear");
	UIInterfaceOrientation newOrientation = [[UIApplication sharedApplication] statusBarOrientation];
	[self willRotateToInterfaceOrientation:newOrientation duration:0.];
}


- (void)viewDidAppear:(BOOL)animated
{
	[super viewDidAppear:animated];

	UIApplication *application = [UIApplication sharedApplication];
	if([application respondsToSelector:@selector(beginReceivingRemoteControlEvents)]) {
		[application beginReceivingRemoteControlEvents];
	}

	[self becomeFirstResponder]; // this enables listening for events

	// update the UI in case we were in the background
	NSNotification *notification = [NSNotification notificationWithName:ASStatusChangedNotification object:self];
	[[NSNotificationCenter defaultCenter] postNotification:notification];
}

- (BOOL)canBecomeFirstResponder
{
	return YES;
}

- (void) willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration
{
	[[NSBundle mainBundle] loadNibNamed: [self getNibNameByOrientation:toInterfaceOrientation]
								  owner: self
								options: nil];
}

- (void) didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation
{
	if(streamer != nil && ([streamer isPlaying] || ![streamer isWaiting])) {
		[self updateUi];
	}
}

- (void)dealloc
{
    [self destroyStreamer];
	[super dealloc];
}

#pragma mark - Actions

- (IBAction) playOrPause:(id)sender
{
    NSLog(@"playOrPause");
	if([self isPlayerLoading]) {
		return;
	}

	[self clearUi:YES];
	if([self isPlaying]) {
		[self setPlayButtonImage];
		[self destroyStreamer];
	} else {
        [self createStreamer];
        [streamer start];

		[self setWaitButtonImage:NO];
    }
}

- (BOOL) isPlayerLoading
{
	return [playPauseButton.currentImage isEqual:[UIImage imageNamed:WAIT_IMAGE_NORMAL]] ||
		[playPauseButton.currentImage isEqual:[UIImage imageNamed:WAIT_IMAGE_PRESSED]];
}

- (BOOL) isPlaying
{
	return [playPauseButton.currentImage isEqual:[UIImage imageNamed:PAUSE_IMAGE_0]] ||
		[playPauseButton.currentImage isEqual:[UIImage imageNamed:PAUSE_IMAGE_PRESSED]];
}

#pragma mark - Notification receivers

- (void)metadataChanged:(NSNotification *)aNotification
{
	NSString *streamArtist;
	NSString *streamTitle;
    NSLog(@"Raw meta data = %@", [[aNotification userInfo] objectForKey:@"metadata"]);
    
	NSArray *metaParts = [[[aNotification userInfo] objectForKey:@"metadata"] componentsSeparatedByString:@"';"];
	NSString *item;
	NSMutableDictionary *hash = [[NSMutableDictionary alloc] init];
	for(item in metaParts) {
		// split the key/value pair
		NSArray *pair = [item componentsSeparatedByString:@"="];
		// don't bother with bad metadata
		if ([pair count] == 2) {
			[hash setObject:[pair objectAtIndex:1] forKey:[pair objectAtIndex:0]];
		}
	}

    NSString *streamString = [hash objectForKey:@"StreamTitle"];
	NSArray *streamParts = [streamString componentsSeparatedByString:@" - "];
	if ([streamParts count] > 0) {
		streamArtist = [[streamParts objectAtIndex:0] substringFromIndex:1];
	} else {
		streamArtist = @"";
	}

	if ([streamParts count] >= 2) {
		streamTitle = [streamParts objectAtIndex:1];
	} else {
		streamTitle = @"";
	}

	NSLog(@"%@ by %@", streamTitle, streamArtist);

	self.currentArtist = streamArtist;
	self.currentTitle = streamTitle;

    [self updateUi];
}

//
// playbackStateChanged:
//
// Invoked when the AudioStreamer
// reports that its playback status has changed.
//
- (void)playbackStateChanged:(NSNotification *)aNotification
{
    StreamingPlayerDelegate *appDelegate = [[UIApplication sharedApplication] delegate];

	[streamer setMeteringEnabled:NO];
	if([streamer isIdle]) {
		[self destroyStreamer];
	}
	if(!appDelegate.uiIsVisible) {
		return;
	}

	if([streamer isWaiting]) {
		[self setWaitButtonImage:YES];
	} else if([streamer isPlaying]) {
		[self setPauseButtonImage];
	} else if([streamer isPaused]) {
		[streamer stop];
		[self setPlayButtonImage];
	} else if([streamer isIdle]) {
		[self setPlayButtonImage];
	}
}

#pragma mark - Other functions

- (void) createStreamer
{
    if(streamer != nil) {
        [self destroyStreamer];
    }

    NSURL *url = [NSURL URLWithString:SERVER_URL];
    streamer = [[AudioStreamer alloc] initWithURL:url];

    [[NSNotificationCenter defaultCenter]
		addObserver:self
		selector:@selector(metadataChanged:)
		name:ASUpdateMetadataNotification
		object:streamer];

    [[NSNotificationCenter defaultCenter]
		addObserver:self
		selector:@selector(playbackStateChanged:)
		name:ASStatusChangedNotification
		object:streamer];
}

- (void) destroyStreamer
{
	if (streamer)
	{
		[[NSNotificationCenter defaultCenter]
         removeObserver:self
         name:ASStatusChangedNotification
         object:streamer];

		[streamer stop];
		[streamer release];
		streamer = nil;
	}
}

- (void) clearUi:(BOOL)resetCurrentValues
{
    NSLog(@"clearUi");

    UIImage *image = [UIImage imageNamed:DEFAULT_COVER_FILENAME];
    coverImageView.image = image;

	if([DeviceUtils isPhone] && ![DeviceUtils isLandscape]) {
		// only titleLabel
		titleLabel.text = @"- UnicaRadio -";
	} else {
		if(![DeviceUtils isPhone] && [DeviceUtils isLandscape]) {
			[self.titleLabel setHidden:YES];
			[self.singerLabel setHidden:YES];
		}

		titleLabel.text = @"";
		singerLabel.text = @"- UnicaRadio -";
	}

	if(resetCurrentValues) {
		currentTitle = @"";
		currentArtist = @"";
	}
}

- (void) updateUi
{
    NSLog(@"updateUi");
    [self clearUi:NO];

	if(streamer == nil || [streamer isIdle]) {
		return;
	}

    // draw cover, title and singer
    AppDelegate *appDelegate = (AppDelegate *) [[UIApplication sharedApplication] delegate];
	if (appDelegate.uiIsVisible) {
		if([DeviceUtils isPhone] && ![DeviceUtils isLandscape]) {
			// only titleLabel
			NSString *currentlyOnAir = self.currentArtist;

			if(self.currentTitle != @"") {
				currentlyOnAir = [NSString stringWithFormat:@"%@ - %@", currentlyOnAir, self.currentTitle];
			} else {
				currentlyOnAir = [NSString stringWithFormat:@"- %@ -", currentlyOnAir];
			}

			self.titleLabel.text = currentlyOnAir;
		} else {
			if(![DeviceUtils isPhone]) {
				[self.titleLabel setHidden:NO];
				[self.singerLabel setHidden:NO];
				self.singerLabel.text = currentArtist;
			} else {
				self.singerLabel.text = [NSString stringWithFormat:@"- %@ -", currentArtist];
			}

			self.titleLabel.text = currentTitle;
		}
        
        if(streamer != nil && ([streamer isPlaying] || [streamer isWaiting])) {
            NSURL *url = [NSURL URLWithString:COVER_URL];
            UIImage *image = [UIImage imageWithData: [NSData dataWithContentsOfURL:url]];
            coverImageView.image = image;
			
			[self setPauseButtonImage];
		} else {
			[self setPlayButtonImage];
        }
	}
}

- (NSString *) getNibNameByOrientation:(UIInterfaceOrientation)interfaceOrientation
{
	oldOrientation = interfaceOrientation;

	NSString *deviceLabel;
    if([DeviceUtils isPhone]) {
		if([DeviceUtils is4InchRetinaIPhone]) {
			deviceLabel = @"568h@2x";
		} else {
			deviceLabel = @"iPhone";
		}
    } else {
        deviceLabel = @"iPad";
    }
	
    NSString *orientationLabel;
    if([DeviceUtils isLandscape:interfaceOrientation] || ![DeviceUtils isPhone]) {
		orientationLabel = @"-landscape";
    } else {
        orientationLabel = @"";
    }
	
	NSString *nibName = [NSString stringWithFormat:@"%@_%@%@", NSStringFromClass([self class]), deviceLabel, orientationLabel];
	return nibName;
}

- (void)setPlayButtonImage
{
	[playPauseButton.layer removeAllAnimations];

	[playPauseButton setImage:[UIImage imageNamed:PLAY_IMAGE_NORMAL] forState:UIControlStateNormal];
	[playPauseButton setImage:[UIImage imageNamed:PLAY_IMAGE_PRESSED] forState:UIControlStateHighlighted];
}


- (void)setPauseButtonImage
{
	[playPauseButton.layer removeAllAnimations];
	
	[playPauseButton setImage:[UIImage imageNamed:PAUSE_IMAGE_0] forState:UIControlStateNormal];
	[playPauseButton setImage:[UIImage imageNamed:PAUSE_IMAGE_PRESSED] forState:UIControlStateHighlighted];
}

- (void)setWaitButtonImage:(BOOL)animate
{
	[playPauseButton.layer removeAllAnimations];
	
	[playPauseButton setImage:[UIImage imageNamed:WAIT_IMAGE_NORMAL] forState:UIControlStateNormal];
	[playPauseButton setImage:[UIImage imageNamed:WAIT_IMAGE_PRESSED] forState:UIControlStateHighlighted];

	if(animate) {
		[self spinButton];
	}
}

//
// spinButton
//
// Shows the spin button when the audio is loading. This is largely irrelevant
// now that the audio is loaded from a local file.
//
- (void)spinButton
{
	[CATransaction begin];
	[CATransaction setValue:(id)kCFBooleanTrue forKey:kCATransactionDisableActions];
	CGRect frame = [playPauseButton frame];
	playPauseButton.layer.anchorPoint = CGPointMake(0.5, 0.5);
	playPauseButton.layer.position = CGPointMake(frame.origin.x + 0.5 * frame.size.width, frame.origin.y + 0.5 * frame.size.height);
	[CATransaction commit];
	
	[CATransaction begin];
	[CATransaction setValue:(id)kCFBooleanFalse forKey:kCATransactionDisableActions];
	[CATransaction setValue:[NSNumber numberWithFloat:2.0] forKey:kCATransactionAnimationDuration];
	
	CABasicAnimation *animation;
	animation = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
	animation.fromValue = [NSNumber numberWithFloat:0.0];
	animation.toValue = [NSNumber numberWithFloat:2 * M_PI];
	animation.timingFunction = [CAMediaTimingFunction functionWithName: kCAMediaTimingFunctionLinear];
	animation.delegate = self;
	animation.repeatCount = -1;
	[playPauseButton.layer addAnimation:animation forKey:@"rotationAnimation"];
	
	[CATransaction commit];
}

- (void)remoteControlReceivedWithEvent:(UIEvent *)event
{
	switch(event.subtype) {
		case UIEventSubtypeRemoteControlTogglePlayPause:
			[streamer stop];
			break;
		case UIEventSubtypeRemoteControlPlay:
			[streamer start];
			break;
		case UIEventSubtypeRemoteControlPause:
			[streamer stop];
			break;
		case UIEventSubtypeRemoteControlStop:
			[streamer stop];
			break;
		default:
			break;
	}
}

@end
