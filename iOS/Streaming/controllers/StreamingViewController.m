//
//  FirstViewController.m
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <QuartzCore/CoreAnimation.h>

#import "StreamingViewController.h"
#import "SettingsViewController.h"
#import "AppDelegate.h"

#import "../libs/audiostreamer/AudioStreamer.h"
#import "../libs/MarqueeLabel/MarqueeLabel.h"

#import "../utils/DeviceUtils.h"
#import "../utils/NetworkUtils.h"

@interface StreamingViewController ()

@end

@implementation StreamingViewController

@synthesize titleLabel;
@synthesize singerLabel;
@synthesize playPauseButton;
@synthesize coverImageView;

@synthesize infos;
@synthesize oldInfos;

@synthesize popover;

@synthesize settingsManager;

#pragma mark - Controller lifecycle

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
	NSLog(@"StreamingViewController - initWithNibName");
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
	
    if (self) {
        self.title = NSLocalizedString(@"CONTROLLER_TITLE_ONAIR", @"");
        self.tabBarItem.image = [UIImage imageNamed:@"onair"];

		if(infos == nil) {
			infos = [[TrackInfos alloc] init];	
		}
		settingsManager = [SettingsManager getInstance];

		UIBarButtonItem *settingsButton = [[UIBarButtonItem alloc] initWithTitle:@"\u2699" style:UIBarButtonItemStylePlain target:self action:@selector(openSettings:)];
		UIFont *f1 = [UIFont fontWithName:@"Helvetica" size:23.0];
		NSDictionary *dict = [[NSDictionary alloc] initWithObjectsAndKeys:f1, UITextAttributeFont, nil];
		[settingsButton setTitleTextAttributes:dict forState:UIControlStateNormal];

		self.navigationItem.rightBarButtonItem = settingsButton;
    }
    NSLog(@"init streaming view controller");
    return self;
}

- (void) openSettings:(id) sender
{
	NSLog(@"Open settings");
	if([DeviceUtils isPhone]) {
		UIViewController *settingsViewController = [SettingsViewController createSettingsController];
		[self presentModalViewController:settingsViewController animated:YES];
	} else {
		[self openSettingsForIPad:sender];
	}
}

- (void) openSettingsForIPad:(id) sender
{
	if(popover == nil) {
		UIViewController *settingsViewController = [SettingsViewController createSettingsController];
		popover = [[UIPopoverController alloc] initWithContentViewController:settingsViewController];
	}

	if([popover isPopoverVisible]) {
		[popover dismissPopoverAnimated:YES];
	} else {
		[popover presentPopoverFromBarButtonItem:sender permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
	}
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

- (void) viewWillDisappear:(BOOL)animated
{
	[super viewWillDisappear:animated];

	oldInfos = [[TrackInfos alloc] init];
	[oldInfos setTrackInfos:infos];
}

- (void) initMarqueeLabel:(MarqueeLabel *)marqueeLabel andUILabel:(UILabel *)label andInitialText:(NSString *) initialText
{
    marqueeLabel.marqueeType = MLContinuous;
	marqueeLabel.animationCurve = UIViewAnimationOptionCurveLinear;
    marqueeLabel.numberOfLines = 1;
    marqueeLabel.opaque = NO;
    marqueeLabel.enabled = YES;
    marqueeLabel.shadowOffset = CGSizeMake(0.0, -1.0);
    marqueeLabel.textAlignment = UITextAlignmentCenter;
    marqueeLabel.textColor = label.textColor;
    marqueeLabel.backgroundColor = [UIColor clearColor];
    marqueeLabel.font = label.font;
    marqueeLabel.text = initialText;
    [self.view addSubview:marqueeLabel];
	[label setHidden:YES];
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
	NSLog(@"willRotateToInterfaceOrientation");
	[[NSBundle mainBundle] loadNibNamed: [self getNibNameByOrientation:toInterfaceOrientation]
								  owner: self
								options: nil];

	titleMarqueeLabel = nil;
	singerMarqueeLabel = nil;
	if([DeviceUtils isPhone]) {
		titleMarqueeLabel = [[MarqueeLabel alloc] initWithFrame:titleLabel.frame rate:80.0f andFadeLength:10.0f];
		titleMarqueeLabel.tag = 101;
		[self initMarqueeLabel:titleMarqueeLabel andUILabel:titleLabel andInitialText:@"- UnicaRadio -"];
		
		if([DeviceUtils isLandscape:toInterfaceOrientation]) {
			singerMarqueeLabel = [[MarqueeLabel alloc] initWithFrame:singerLabel.frame rate:80.0f andFadeLength:10.0f];
			singerMarqueeLabel.tag = 101;
			[self initMarqueeLabel:singerMarqueeLabel andUILabel:singerLabel andInitialText:@""];
		}
	}
}

- (void) didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation
{
	NSLog(@"didRotateFromInterfaceOrientation");
	if(streamer != nil && ([streamer isPlaying] || ![streamer isWaiting])) {
		[self updateUi];
	}
}

- (void)dealloc
{
    [self destroyStreamer];
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

		[infos clean];
		[NSObject cancelPreviousPerformRequestsWithTarget:self
												 selector:@selector(updateCover)
												   object:nil];
	} else {
		if(![self isConnectionOK]) {
			return;
		}

        [self createStreamer];
        [streamer start];

		[self setWaitButtonImage:NO];
    }
}

- (BOOL) isConnectionOK
{
	if(![NetworkUtils isConnected]) {
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle: @"Non connesso"
														message: @"Oh oh... non sei connesso alla rete. Riprova."
													   delegate: nil
											  cancelButtonTitle: @"OK"
											  otherButtonTitles: nil];
		[alert show];

		return NO;
	}

	NetworkType enabledNetworkType = [settingsManager getNetworkType];
	if(enabledNetworkType == WIFI_ONLY && ![NetworkUtils isConnectedToWiFi]) {
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle: @"Non connesso"
														message: @"Oh oh... non sei connesso in wifi. Controlla le impostazioni se vuoi usare questo tipo di rete"
													   delegate: nil
											  cancelButtonTitle: @"OK"
											  otherButtonTitles: nil];
		[alert show];

		return NO;
	}

	return YES;
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

	[self updateTrackInfosWithAutor:streamArtist andTitle:streamTitle];
}

- (void) updateTrackInfosWithAutor:(NSString *)author andTitle:(NSString *)title
{
	if(oldInfos != nil &&
	   [author isEqualToString:oldInfos.author] &&
	   [title isEqualToString:oldInfos.title]) {
		infos = oldInfos;
		oldInfos = nil;

		[self updateUi];
		return;
	}

	[infos setAuthor:author];
	[infos setTitle:title];
	[infos setCover:nil];
	[self updateUi];

	[self performSelector:@selector(updateCover) withObject:nil afterDelay:5];
}

//
// playbackStateChanged:
//
// Invoked when the AudioStreamer
// reports that its playback status has changed.
//
- (void)playbackStateChanged:(NSNotification *)aNotification
{
    AppDelegate *appDelegate = (AppDelegate *) [[UIApplication sharedApplication] delegate];

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
		//titleLabel.text = @"- UnicaRadio -";
		titleMarqueeLabel.text = @"- UnicaRadio -";
		[titleMarqueeLabel setTextAlignment:UITextAlignmentCenter];
	} else {
		if(![DeviceUtils isPhone]) {
			[self.titleLabel setHidden:YES];
			[self.singerLabel setHidden:YES];

			titleLabel.text = @"";
			singerLabel.text = @"- UnicaRadio -";
		} else {
			titleMarqueeLabel.text = @"";
			[titleMarqueeLabel setTextAlignment:UITextAlignmentCenter];
			singerMarqueeLabel.text = @"- UnicaRadio -";
			[singerMarqueeLabel setTextAlignment:UITextAlignmentCenter];
		}
	}

	if(resetCurrentValues) {
		[infos clean];
	}
}

- (void) updateUi
{
    NSLog(@"updateUi");
    [self clearUi:NO];

	if(streamer == nil || [streamer isIdle]) {
		[infos clean];
		return;
	}
	if([infos isClean]) {
		return;
	}

    // draw cover, title and singer
    AppDelegate *appDelegate = (AppDelegate *) [[UIApplication sharedApplication] delegate];
	if (appDelegate.uiIsVisible) {
		if([DeviceUtils isPhone] && ![DeviceUtils isLandscape]) {
			// only titleLabel
			NSString *currentlyOnAir = infos.author;

			if(![infos.title isEqualToString:@""]) {
				currentlyOnAir = [NSString stringWithFormat:@"%@ - %@", currentlyOnAir, infos.title];
			} else {
				currentlyOnAir = [NSString stringWithFormat:@"- %@ -", currentlyOnAir];
			}

			titleMarqueeLabel.text = currentlyOnAir;
			[titleMarqueeLabel setTextAlignment:UITextAlignmentCenter];
		} else {
			if(![DeviceUtils isPhone]) {
				[self.titleLabel setHidden:NO];
				[self.singerLabel setHidden:NO];
				self.singerLabel.text = infos.author;
				self.titleLabel.text = infos.title;
			} else {
				singerMarqueeLabel.text = [NSString stringWithFormat:@"- %@ -", infos.author];
				[singerMarqueeLabel setTextAlignment:UITextAlignmentCenter];
				titleMarqueeLabel.text = infos.title;
				[titleMarqueeLabel setTextAlignment:UITextAlignmentCenter];
			}
		}

		if(infos.cover != nil) {
			coverImageView.image = infos.cover;
		}

        if(streamer != nil && ([streamer isPlaying] || [streamer isWaiting])) {
			[self setPauseButtonImage];
		} else {
			[self setPlayButtonImage];
        }
	}
}

- (void) updateCover
{
	if(![self isPlaying]) {
		[infos setCover:nil];
		[self clearUi:YES];
		return;
	}

	NSURL *url = [NSURL URLWithString:COVER_URL];
	[infos setCover:[UIImage imageWithData: [NSData dataWithContentsOfURL:url]]];

	if(![self isPlaying]) {
		[infos setCover:nil];
		[self clearUi:YES];
		return;
	}

	coverImageView.image = infos.cover;
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
