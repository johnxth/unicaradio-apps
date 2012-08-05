//
//  FirstViewController.m
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "StreamingViewController.h"
#import "AppDelegate.h"

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
	//NSString *nibName = [self getNibNameByOrientation:[[UIApplication sharedApplication] statusBarOrientation]];
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
	
    if (self) {
        self.title = NSLocalizedString(@"On Air", @"On Air");
        self.tabBarItem.image = [UIImage imageNamed:@"onair"];
    }
    NSLog(@"init streaming view controller");
    return self;
}
							
- (void)viewDidLoad
{
    [super viewDidLoad];
	NSLog(@"StreamingViewController - viewDidLoad");
	// Do any additional setup after loading the view, typically from a nib.

	if(streamer == nil || ![streamer isPlaying] || ![streamer isWaiting]) {
		[self clearCurrentData];
		[self clearUi];
	}

	[self updateUi];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (void) viewWillAppear:(BOOL)animated
{
	NSLog(@"StreamingViewController - viewWillAppear");
	UIInterfaceOrientation newOrientation = [[UIApplication sharedApplication] statusBarOrientation];
	//if(oldOrientation != newOrientation) {
		//NSLog(@"StreamingViewController - viewWillAppear: updating nib");
		[self willRotateToInterfaceOrientation:newOrientation duration:0.];
	//} else {
		//NSLog(@"StreamingViewController - viewWillAppear: orientation OK!");
	//}
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
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
	[self updateUi];
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
    [self clearCurrentData];

    if([streamer isPlaying]) {
        [streamer stop];
        [self destroyStreamer];
    } else {
        [self createStreamer];
        [streamer start];
    }
    [self updateUi];
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
	for (item in metaParts) {
		// split the key/value pair
		NSArray *pair = [item componentsSeparatedByString:@"="];
		// don't bother with bad metadata
		if ([pair count] == 2)
			[hash setObject:[pair objectAtIndex:1] forKey:[pair objectAtIndex:0]];
	}
    
	// do something with the StreamTitle
	//NSString *streamString = [[hash objectForKey:@"StreamTitle"] stringByReplacingOccurrencesOfString:@"'" withString:@""];
    NSString *streamString = [hash objectForKey:@"StreamTitle"];
	
	NSArray *streamParts = [streamString componentsSeparatedByString:@" - "];
	if ([streamParts count] > 0) {
		streamArtist = [[streamParts objectAtIndex:0] substringFromIndex:1];
	} else {
		streamArtist = @"";
	}
	// this looks odd but not every server will have all artist hyphen title
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
	/*
    iPhoneStreamingPlayerAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
	if ([streamer isWaiting])
	{
		if (appDelegate.uiIsVisible) {
			[levelMeterView updateMeterWithLeftValue:0.0 
                                          rightValue:0.0];
			[streamer setMeteringEnabled:NO];
			[self setButtonImage:[UIImage imageNamed:@"loadingbutton.png"]];
		}
	}
	else if ([streamer isPlaying])
	{
		if (appDelegate.uiIsVisible) {
			[streamer setMeteringEnabled:YES];
			[self setButtonImage:[UIImage imageNamed:@"stopbutton.png"]];
		}
	}
	else if ([streamer isPaused]) {
		if (appDelegate.uiIsVisible) {
			[levelMeterView updateMeterWithLeftValue:0.0 
                                          rightValue:0.0];
			[streamer setMeteringEnabled:NO];
			[self setButtonImage:[UIImage imageNamed:@"pausebutton.png"]];
		}
	}
	else if ([streamer isIdle])
	{
		if (appDelegate.uiIsVisible) {
			[levelMeterView updateMeterWithLeftValue:0.0 
                                          rightValue:0.0];
			[self setButtonImage:[UIImage imageNamed:@"playbutton.png"]];
		}
		[self destroyStreamer];
	}
    */
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

- (void) clearUi
{
    NSLog(@"clearUi");

    UIImage *image = [UIImage imageNamed:DEFAULT_COVER_FILENAME];
    coverImageView.image = image;
    titleLabel.text = @"";
    singerLabel.text = @"- Unicaradio -";
}

- (void) clearCurrentData
{
    NSLog(@"clearCurrentData");
    self.currentTitle = @"";
    self.currentArtist = @"- Unicaradio -";
}

- (void) updateUi
{
    NSLog(@"updateUi");
    [self clearUi];

    // draw cover, title and singer
    AppDelegate *appDelegate = (AppDelegate *) [[UIApplication sharedApplication] delegate];
	if (appDelegate.uiIsVisible) {
		singerLabel.text = self.currentArtist;
        titleLabel.text = self.currentTitle;
        
        if(streamer != nil && ([streamer isPlaying] || [streamer isWaiting])) {
            NSURL *url = [NSURL URLWithString:COVER_URL];
            UIImage *image = [UIImage imageWithData: [NSData dataWithContentsOfURL:url]];
            coverImageView.image = image;
			
			[playPauseButton setImage:[UIImage imageNamed:PAUSE_IMAGE_NORMAL] forState:UIControlStateNormal];
			[playPauseButton setImage:[UIImage imageNamed:PAUSE_IMAGE_PRESSED] forState:UIControlStateHighlighted];
		} else {
			[playPauseButton setImage:[UIImage imageNamed:PLAY_IMAGE_NORMAL] forState:UIControlStateNormal];
			[playPauseButton setImage:[UIImage imageNamed:PLAY_IMAGE_PRESSED] forState:UIControlStateHighlighted];
        }
	}
}

- (NSString *) getNibNameByOrientation:(UIInterfaceOrientation)interfaceOrientation
{
	oldOrientation = interfaceOrientation;

	NSString *deviceLabel;
    if([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) {
        deviceLabel = @"iPhone";
    } else {
        deviceLabel = @"iPad";
    }
	
    NSString *orientationLabel;
    if([self isLandscape:interfaceOrientation]) {
		orientationLabel = @"-landscape";
    } else {
        orientationLabel = @"";
    }
	
	NSString *nibName = [NSString stringWithFormat:@"%@_%@%@", NSStringFromClass([self class]), deviceLabel, orientationLabel];
	//NSLog([NSString stringWithFormat:@"orientation: %d", interfaceOrientation]);
	//NSLog([NSString stringWithFormat:@"%@ %@", @"StreamingViewController - getNibNameByOrientation: nibName is ", nibName]);
	return nibName;
}

- (BOOL) isLandscape:(UIInterfaceOrientation)interfaceOrientation
{
	return UIInterfaceOrientationIsLandscape(interfaceOrientation) || interfaceOrientation == UIInterfaceOrientationLandscapeLeft || interfaceOrientation == UIInterfaceOrientationLandscapeRight;
}

@end
